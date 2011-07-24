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
package verdandi.ui.workdayeditor

import verdandi.ui.CostUnitUIExtension
import verdandi.model.VerdandiModel
import verdandi.ui.TextResources
import verdandi.model.CostUnit
import verdandi.ui.swing.DynamicMenu
import verdandi.event._
import scala.swing.MenuItem

import verdandi.ui.swing.PopupMenu

class WorkDayPopup(val editor: WorkDayEditor) extends PopupMenu {

  val newMenu = new DynamicMenu(TextResources.getText("action.newworkrecord.label"))
  val changeMenu = new DynamicMenu(TextResources.getText("action.changecostunit.label"))

  reactions += {
    case selChanged: PersonalCostUnitSelectionChanged => reload()
    case beforePopup: BeforePopupEvent => setVisibility(beforePopup.recordUnderCursor.isDefined)
  }

  // init
  {
    add(newMenu);
    add(changeMenu)
    add(new MenuItem(new DeleteWorkRecordAction(editor)))
    add(new MenuItem(new StartTrackingCurrentRecordAction(editor)))
    add(new MenuItem(new StopTrackingAction(editor)))
    listenTo(EventBroadcaster)
    listenTo(editor)
    reload()
  }

  def setVisibility(hasRecordUnderCursor: Boolean) {
    newMenu.enabled = !hasRecordUnderCursor
    changeMenu.enabled = hasRecordUnderCursor
  }

  def reload() {
    newMenu.clear()
    changeMenu.clear()
    val costUnitList: List[CostUnit] = VerdandiModel.costUnitUserStorage.getOwnSelection.sort(CostUnit.byNameSorter)
    val newItemList = CostUnitUIExtension.adapter.getCostUnitSelector(costUnitList, (cu: CostUnit) => new NewWorkRecordAction(cu, editor))
    newItemList.foreach(newMenu.add(_))
    val changeItemList = CostUnitUIExtension.adapter.getCostUnitSelector(costUnitList, (cu: CostUnit) => new ChangeWorkRecordsCostunitAction(cu, editor))
    changeItemList.foreach(changeMenu.add(_))
  }

}
