/*******************************************************************************
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
 ******************************************************************************/
package verdandi.ui

import net.infonode.tabbedpanel.titledtab.TitledTab
import net.infonode.tabbedpanel.Tab
import net.infonode.tabbedpanel.TabbedPanel
import verdandi.ui.report.ReportingPanel
import java.awt.SystemTray
import verdandi.model.VerdandiConfiguration
import java.io.FileOutputStream
import java.io.ObjectOutputStream
import verdandi.event.EventBroadcaster
import verdandi.event.ApplicationShutdown
import java.io.FileInputStream
import java.io.ObjectInputStream
import verdandi.VerdandiHelper
import java.io.File
import scala.swing.Component
import scala.swing.BorderPanel
import verdandi.ui.report.AnnotatedWorkRecordView
import verdandi.model.VerdandiModel
import verdandi.ui._
import verdandi.ui.workdayeditor.WorkDayEditorModel
import verdandi.ui.workdayeditor.WorkDayEditorPanel
import verdandi.ui.workdayeditor.WorkDayEditor
import net.infonode.docking.DockingWindow
import net.infonode.docking.RootWindow
import net.infonode.docking.SplitWindow
import net.infonode.docking.TabWindow
import net.infonode.docking.View
import net.infonode.docking.properties.RootWindowProperties
import net.infonode.docking.theme._
import net.infonode.docking.util.DockingUtil
import net.infonode.docking.util.ViewMap
import net.infonode.docking.util.PropertiesUtil
import net.infonode.util.Direction
import com.weiglewilczek.slf4s.Logging

class InfoNodeAppWindow extends AbstractMainWindow with Logging {

  private lazy val stateFile = new File(VerdandiModel.conf.getBasedir, "windowstate")

  // the second parm (null) is the icon
  val workDayEditorView = new View(TextResources("tabname.day.editor"), null, new WorkDayEditorPanel().getPeer());
  val summariesView = new View(TextResources("tabname.week.summaryview"), null, new SummaryPanel());
  val personalProjectsView = new View(TextResources("tabname.personal.projects"), null, new PersonalProjectSelectionPanel());
  val projectEditorView = new View(TextResources("tabname.project.editor"), null, new ProjectViewerPanel());
  val reportView = new View(TextResources("tabname.report.durations"), null, new ReportingPanel().peer);
  val settingsView = new View(TextResources("tabname.settings"), null, new SettingsPanel().peer);

  private lazy val viewMap = new ViewMap()

  private lazy val rootWindow = DockingUtil.createRootWindow(viewMap, true)

  override def init() {

    reactions += {
      case shutdown: ApplicationShutdown => writeWindowState()
    }

    viewMap.addView(1, workDayEditorView);
    viewMap.addView(2, summariesView);
    viewMap.addView(3, personalProjectsView);
    viewMap.addView(4, projectEditorView);
    viewMap.addView(5, reportView);
    viewMap.addView(6, settingsView);

    val rootWindowProperties = new RootWindowProperties()

    rootWindow.getRootWindowProperties().addSuperObject(rootWindowProperties);

    rootWindowProperties.addSuperObject(new ShapedGradientDockingTheme()
      .getRootWindowProperties());

    val titleBarStyleProperties = PropertiesUtil.createTitleBarStyleRootWindowProperties()
    rootWindow.getRootWindowProperties().addSuperObject(titleBarStyleProperties);

    val tabPanel = new TabbedPanel()
    tabPanel.getProperties.setTabAreaOrientation(Direction.DOWN)
    val tab = new TitledTab(null, Icons.getIcon("tab.tracking.icon"), rootWindow, null)
    tabPanel.addTab(tab)

    contents = new BorderPanel() {
      //add(Component.wrap(rootWindow), BorderPanel.Position.Center)
      add(Component.wrap(tabPanel), BorderPanel.Position.Center)
    }
    listenTo(EventBroadcaster)
    initWindowState()
    pack()

    // only start hidden if system tray is supported.
    visible = !(VerdandiConfiguration.startHidden && SystemTray.isSupported)
    VerdandiModel.setDefaultParentFrame(peer)
  }

  private def initWindowState() {
    def initialwindowState() {
      val defaultTabWindow = new TabWindow(Array[DockingWindow](personalProjectsView, reportView))
      val leftRight = new SplitWindow(true, 0.3f, workDayEditorView,
        new SplitWindow(false, summariesView, defaultTabWindow));
      rootWindow.setWindow(leftRight);
      rootWindow.getWindowBar(Direction.DOWN).setEnabled(true);
    }

    try {
      if (stateFile.exists()) {
        logger.debug("Restoring windowstate")
        VerdandiHelper.withCloseable(new ObjectInputStream(new FileInputStream(stateFile))) { is =>
          rootWindow.read(is)
        }
      } else initialwindowState()
    } catch {
      case t: Throwable => {
        logger.warn("Cannot restore view. Will have to recreate " + t);
        initialwindowState()
      }
    }
  }

  private def writeWindowState() {
    try {
      VerdandiHelper.withCloseable(new ObjectOutputStream(new FileOutputStream(stateFile))) { out =>
        logger.debug("writing window state")
        rootWindow.write(out);
      }
    } catch {
      case t: Throwable => logger.warn("Cannot write view state. Will have to recreate on next start" + t);
    }
  }
}
