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
package verdandi.ui.workdayeditor

import verdandi.model.WorkRecord
import verdandi.Predef._
import verdandi.RichCalendar
import verdandi.event._
import verdandi.model.VerdandiModel
import verdandi.model.CostUnit
import verdandi.ui._
import verdandi.ui.swing._
import scala.swing.Orientation
import scala.swing.BoxPanel
import scala.swing.{ BorderPanel, Component, Button, Publisher, Label, ScrollPane, MenuBar, MenuItem, TextArea }
import scala.swing.event.{ Event }
import org.jdesktop.swingx.{ JXDatePicker }
import java.awt.Color
import java.util.{ Date, Calendar }
import java.beans.{ PropertyChangeEvent, PropertyChangeListener }
import javax.swing.{ Box, BorderFactory }
import java.awt.BorderLayout
import com.weiglewilczek.slf4s.Logging

class WorkDayEditorPanel(wde: WorkDayEditor = new WorkDayEditor) extends BorderPanel {

  object ControlPanel extends RichBoxPanel(Orientation.Horizontal) {
    val dateChooser = new DateChooser()
    val startTrackingMenu = new DynamicMenu(Icons.getIcon("icon.workdayeditor.track.start"))
    val stopButton = new Button(new StopTracking(wde))

    reactions += {
      case evt: PersonalCostUnitSelectionChanged => populateStartMenu()
      case evt: DatePicker.DayChanged => dayChanged(evt)
    }
    xLayoutAlignment = 0.5
    yLayoutAlignment = 0.5

    init()

    def init() {
      populateStartMenu()
      val menu = new MenuBar() {
        contents += startTrackingMenu
      }
      menu.yLayoutAlignment_=(0.5)

      contents += createHorizontalGlue
      contents += dateChooser
      contents += createHorizontalStrut(10)
      contents += menu
      contents += createHorizontalStrut(10)
      contents += stopButton
      contents += createHorizontalGlue

      border = BorderFactory.createEmptyBorder(5, 0, 5, 0)

      wde.model.listenTo(startTrackingMenu)
      wde.model.listenTo(dateChooser)
      listenTo(EventBroadcaster)
      listenTo(dateChooser)
    }

    private def populateStartMenu() {
      startTrackingMenu.clear()
      val costUnitList: List[CostUnit] = VerdandiModel.costUnitUserStorage.getOwnSelection.sort(CostUnit.byNameSorter)
      val itemList = CostUnitUIExtension.adapter.getCostUnitSelector(costUnitList, (cu: CostUnit) => new StartTrackingAction(wde, cu))
      itemList.foreach(startTrackingMenu.add(_))
    }

    private def dayChanged(evt: DatePicker.DayChanged) {
      val today = RichCalendar()
      val newDate = RichCalendar(evt.day)
      startTrackingMenu.enabled = today.get(Calendar.DAY_OF_YEAR) == newDate.get(Calendar.DAY_OF_YEAR)
    }

  }

  object CollapsibleWorkRecordEditor extends SXCollapsiblePane with WorkRecordEditorView with Logging {
    import scala.swing.GridBagPanel._
    val annotationText = new TextArea(3, 15)

    val costUnitSelectorModel: MutableComboBoxModel[CostUnit] = new MutableComboBoxModel(List())

    var currentRecord: WorkRecord = _

    initControls()

    def initControls() {
      val c = RichGridbagConstraints.default

      val cuSelPanel = new RichBoxPanel(Orientation.Horizontal) {
        contents += new Label(TextResources.getText("label.costunit"))
        contents += createHorizontalStrut(5)
        contents += new MutableComboBox(costUnitSelectorModel)
        border = BorderFactory.createEmptyBorder(2, 2, 2, 2)
      }

      add(cuSelPanel, c.withFill(Fill.Horizontal).withWeightX(0.5).withGridWidth(2))
      add(new ScrollPane(annotationText), c.withGridX(0).withGridY(1).withGridWidth(2).withFill(Fill.Both))
      add(new OKCancelPanel(commit, cancelled), c.withGridY(2))

    }

    def commit() {
      currentRecord.costUnit = costUnitSelectorModel.getSelectedItem.asInstanceOf[CostUnit]
      currentRecord.annotation = annotationText.text
      VerdandiModel.workRecordStorage.save(currentRecord)
      close()
    }
    def cancelled() {
      close()
    }

    def edit(rec: WorkRecord) {
      currentRecord = rec
      costUnitSelectorModel.items = VerdandiModel.costUnitUserStorage.getOwnSelection
      costUnitSelectorModel.setSelectedItem(currentRecord.costUnit)
      annotationText.text = currentRecord.annotation
      logger.debug("Decollapsing")
      collapsed = false
    }
    /** Aborted by an action outside the editor */
    def close() {
      collapsed = true
    }
  }

  def getPeer() = peer.asInstanceOf[javax.swing.JPanel]

  initControls()

  private def initControls() {
    val scrl = new ScrollPane(wde)
    wde.workRecordEditor = CollapsibleWorkRecordEditor
    add(scrl, BorderPanel.Position.Center)
    add(ControlPanel, BorderPanel.Position.North)
    CollapsibleWorkRecordEditor.collapsed = true
    add(CollapsibleWorkRecordEditor, BorderPanel.Position.South)
  }

}

