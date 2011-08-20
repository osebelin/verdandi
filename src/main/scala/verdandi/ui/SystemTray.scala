/**
 * *****************************************************************************
 * Copyright 2010 Olaf Sebelin
 *
 * This file is part of Verdandi.
 *
 * Verdandi is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Verdandi is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Verdandi.  If not, see <http://www.gnu.org/licenses/>.
 * ****************************************************************************
 */
package verdandi.ui
import javax.swing.JFrame
import java.awt.{ SystemTray, TrayIcon, Frame => AWTFrame }
import java.awt.event.{ MouseListener, MouseEvent => AWTMouseEvent }
import scala.swing.{ Frame, MenuItem, Action, Reactor }
import verdandi.ui.swing.{ PopupMenu, DynamicMenu }
import verdandi.event._
import verdandi.model._

import com.weiglewilczek.slf4s.Logging
/** The system tray icon and its menu */
class VerdandiSystemTray(mainWindow: JFrame) extends Reactor with Logging {
  if (SystemTray.isSupported) init()

  private lazy val okImage = Icons.getIcon("icon.tray.ok").getImage
  private lazy val warnImage = Icons.getIcon("icon.tray.warn").getImage

  private var trayIcon: TrayIcon = _

  reactions += {
    case start: StartTrackingWorkRecordEvent => startTracking(start)
    case stop: StopTrackingWorkRecordEvent => stopTracking(stop)
  }

  listenTo(EventBroadcaster)

  private def startTracking(evt: StartTrackingWorkRecordEvent) {
    trayIcon.setImage(okImage)
    trayIcon.setToolTip(TextResources.getText("tray.tracking.tooltip", Map("COSTUNIT" -> evt.workRecord.getAssociatedProject())))
  }

  private def stopTracking(evt: StopTrackingWorkRecordEvent) {
    trayIcon.setImage(warnImage)
    trayIcon.setToolTip(TextResources.getText("tray.nottracking.tooltip"))
  }

  private object TrayListener extends MouseListener {

    val popup = new TrayPopup()

    def mouseClicked(evt: AWTMouseEvent) = {
      if (evt.getButton() == AWTMouseEvent.BUTTON1) {
        val wasVisible = mainWindow.isVisible()
        mainWindow.setVisible(!wasVisible)
        if (wasVisible) {
          mainWindow.setState(AWTFrame.ICONIFIED)
        } else {
          mainWindow.setState(AWTFrame.NORMAL)
          mainWindow.toFront()
        }
      }
    }
    def mouseEntered(evt: AWTMouseEvent) = {}
    def mouseExited(evt: AWTMouseEvent) = {}
    def mousePressed(evt: AWTMouseEvent) = showPopup(evt)
    def mouseReleased(evt: AWTMouseEvent) = showPopup(evt)
    private def showPopup(evt: AWTMouseEvent) = if (evt.isPopupTrigger) {
      //buildStartActionMenu();
      popup.location = new java.awt.Point(evt.getX(), evt.getY())
      popup.invoker = popup
      popup.visible = true
    }
  }

  private def init() {
    trayIcon = new TrayIcon(warnImage, TextResources.getText("tray.nottracking.tooltip"), null)
    // at least for linux
    trayIcon.setImageAutoSize(false)
    trayIcon.addMouseListener(TrayListener)
    SystemTray.getSystemTray().add(trayIcon)
  }

}

/** The tray ordered start of tracking work. There is a similar action in the workdayeditor. */
class TrayStartTrackingAction(val costunit: CostUnit) extends Action(costunit.toString) {
  def apply() = EventBroadcaster.publish(StartTrackingRequest(costunit))
}

/** The tray ordered to tops the current tracking. There is a similar action in the workdayeditor. */
class TrayStopTrackingAction extends Action(TextResources.getText("tray.stop.tracking")) with Reactor {
  reactions += {
    case _ => { enabled = true }
  }
  listenTo(EventBroadcaster)

  def apply() = EventBroadcaster.publish(StopTrackingRequest())
}

// FIXME There is a QuitAction in verdandi.ui.action. But it has Mnemonics. 
class TrayQuitAction extends Action(TextResources.getText("tray.quit")) {
  def apply() = System.exit(0);
}

/** PopupMenu */
class TrayPopup extends PopupMenu with Reactor {
  val newMenu = new DynamicMenu(TextResources.getText("tray.start.tracking"))

  reactions += {
    case evt: CostUnitEvent => reload()
    case evt: PersonalCostUnitSelectionChanged => reload()
  }

  {
    listenTo(EventBroadcaster)
    add(newMenu)
    add(new MenuItem(new TrayStopTrackingAction()))
    add(new MenuItem(new TrayQuitAction()))
    reload();
  }

  def reload() {
    newMenu.clear()
    val costUnitList: List[CostUnit] = VerdandiModel.costUnitUserStorage.getOwnSelection.sort(CostUnit.byNameSorter)
    val newItemList = CostUnitUIExtension.adapter.getCostUnitSelector(costUnitList, (cu: CostUnit) => new TrayStartTrackingAction(cu))
    newItemList.foreach(newMenu.add(_))
  }

}
