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

import verdandi.model.CostUnit
import scala.swing.CheckBox
import scala.swing.{ Label, ComboBox }
import scala.swing.GridBagPanel._
import verdandi.event._
import verdandi.model.{ VerdandiConfiguration, VerdandiModel }
import verdandi.ui.swing._
import scala.swing.TextField

class SettingsPanel extends RichGridBagPanel {

  val c = new RichGridbagConstraints()
    .withInsets(2, 2, 2, 2).withGridX(0).withGridY(0).withFill(Fill.None).withWeightX(0.0).withWeightY(0.0)
    .withAnchor(Anchor.West)

  val okCancelPanel =
    new OKCancelPanel(commit, revert, TextResources("settingseditor.label.commit"),
      TextResources("settingseditor.label.revert"))

  val minPixelsPerSlot = new TextField(2)

  val firstWorkHour =
    new ComboBox(1 to 23)

  val lastWorkHour =
    new ComboBox(1 to 23)

  val startHidden =
    new CheckBox(TextResources("settingseditor.label.starthidden"))

  val startTrackingOnLoad =
    new CheckBox(TextResources("settingseditor.label.stratrackingonload"))

  val costUnitSelectorModel: MutableComboBoxModel[CostUnit] =
    new MutableComboBoxModel(List())

  val costUnitSelector =
    new MutableComboBox(costUnitSelectorModel)

  val exitOnClose =
    new CheckBox(TextResources("settingseditor.label.exitonclose"))

  minPixelsPerSlot.inputVerifier = (c => c.asInstanceOf[TextField].text.matches("[0-9]*"))

  add(new Label(TextResources("settingseditor.label.minPixelPerSlot")), c)
  add(minPixelsPerSlot, c.withGridXInc)

  add(new Label(TextResources("settingseditor.workday.lower.boundary")), c.withGridYInc.withGridXDec)
  add(new Label(TextResources("settingseditor.workday.upper.boundary")), c.withGridYInc)
  add(firstWorkHour, c.withGridYDec.withGridXInc)
  add(lastWorkHour, c.withGridYInc)

  add(startHidden, c.withGridYInc.withGridX(0).withGridWidth(2))

  add(startTrackingOnLoad, c.withGridYInc.withGridX(0).withGridWidth(1))
  add(costUnitSelector, c.withGridXInc)

  add(exitOnClose, c.withGridYInc.withGridX(0).withGridWidth(2))

  add(okCancelPanel, c.withGridYInc.withAnchor(Anchor.SouthEast).withInsets(10, 0, 2, 2))

  reactions += {
    case _: CostUnitEvent => loadCostUnits()
    case _: PersonalCostUnitSelectionChanged => loadCostUnits()
  }

  loadCostUnits()
  revert()

  def loadCostUnits() {
    costUnitSelectorModel.items = VerdandiModel.costUnitUserStorage.getOwnSelection
    val cuToTrack = VerdandiConfiguration.costUnitToTrackOnLoad
    val selecteditem = costUnitSelectorModel.items.find(cu => cu.id == cuToTrack)
    if (selecteditem.isDefined)
      costUnitSelectorModel.setSelectedItem(selecteditem.get)
  }

  def commit() {
    VerdandiConfiguration.WorkDayEditorConfiguration.firstHour = firstWorkHour.selection.item
    VerdandiConfiguration.WorkDayEditorConfiguration.lastHour = lastWorkHour.selection.item
    VerdandiConfiguration.startHidden = startHidden.selected
    VerdandiConfiguration.startTrackingOnLoad = startTrackingOnLoad.selected
    if (costUnitSelectorModel.getSelectedItem != null)
      VerdandiConfiguration.costUnitToTrackOnLoad = costUnitSelectorModel.getSelectedItem.asInstanceOf[CostUnit].id
    VerdandiConfiguration.exitOnClose = exitOnClose.selected
    VerdandiConfiguration.WorkDayEditorConfiguration.minPixelsPerSlot = minPixelsPerSlot.text.toInt
    EventBroadcaster.publish(ConfigurationChangedEvent())
  }

  def revert() {
    firstWorkHour.selection.item = VerdandiConfiguration.WorkDayEditorConfiguration.firstHour
    lastWorkHour.selection.item = VerdandiConfiguration.WorkDayEditorConfiguration.lastHour
    startHidden.selected = VerdandiConfiguration.startHidden
    startTrackingOnLoad.selected = VerdandiConfiguration.startTrackingOnLoad
    exitOnClose.selected = VerdandiConfiguration.exitOnClose
    minPixelsPerSlot.text = VerdandiConfiguration.WorkDayEditorConfiguration.minPixelsPerSlot.toString
  }

}
