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

import verdandi.ui.Icons
import verdandi.ui.TextResources
import verdandi.model._
import scala.swing.{ Action, Reactor }
import scala.swing.event.{ MousePressed, MouseReleased }
import java.util.Date
import com.weiglewilczek.slf4s.Logging

abstract class WorkDayEditorAction(val editor: WorkDayEditor, labelKey: String) extends Action(TextResources.getText(labelKey)) with Logging with Reactor {
  listenTo(editor)
  var currentRecord: Option[EditableWorkRecord] = None
  var currentTime: Date = _
  reactions += {
    case evt: BeforePopupEvent => {
      currentRecord = evt.recordUnderCursor
      currentTime = evt.time
      handleEvent(evt)
    }
  }

  def handleEvent(evt: BeforePopupEvent)
}

class NewWorkRecordAction(costUnit: CostUnit, _ed: WorkDayEditor) extends WorkDayEditorAction(_ed, costUnit.toString) {

  def handleEvent(evt: BeforePopupEvent) = enabled = evt.recordUnderCursor.isEmpty

  override def apply() {
    logger.debug("New Work record at " + currentTime)
    VerdandiModel.workRecordStorage.newWorkRecord(costUnit, currentTime)
  }
}

class ChangeWorkRecordsCostunitAction(costUnit: CostUnit, _ed: WorkDayEditor) extends WorkDayEditorAction(_ed, costUnit.toString) {

  def handleEvent(evt: BeforePopupEvent) = enabled = evt.recordUnderCursor.isDefined

  override def apply() {
    currentRecord.get.rec.costUnit = costUnit
    VerdandiModel.workRecordStorage.save(currentRecord.get.rec)
  }
}

class DeleteWorkRecordAction(_ed: WorkDayEditor) extends WorkDayEditorAction(_ed, "action.delete_workrecord.label") with Reactor {

  def handleEvent(evt: BeforePopupEvent) = enabled = evt.recordUnderCursor.isDefined

  override def apply() = currentRecord match {
    case None => logger.warn("No current work record")
    case Some(wr) => VerdandiModel.workRecordStorage.delete(wr.workRecord)
  }
}

class StartTrackingCurrentRecordAction(_ed: WorkDayEditor) extends WorkDayEditorAction(_ed, "action.starttrackingcurrentrecord.label") with Reactor {
  def handleEvent(evt: BeforePopupEvent) = enabled = evt.recordUnderCursor.isDefined

  override def apply() = currentRecord match {
    case None => logger.warn("No current work record")
    case Some(wr) => {
      wr.startTracking()
      editor.repaint()
    }
  }

}
class StopTrackingAction(_ed: WorkDayEditor) extends WorkDayEditorAction(_ed, "action.stoptracking.label") with Reactor {
  def handleEvent(evt: BeforePopupEvent) = {
    enabled = editor.editableRecords.find(_.tracking == true).isDefined
  }

  override def apply() = {
    editor.stopTracking()
    editor.repaint()
  }

}

class StartTrackingAction(val editor: WorkDayEditor, costUnit: CostUnit) extends Action(costUnit.toString) with Logging with Reactor {
  override def apply() = {
    editor.startTracking(costUnit)
  }
}

class StopTracking(val editor: WorkDayEditor) extends Action("") {
  icon = Icons.getIcon("icon.workdayeditor.track.stop")
  override def apply() = {
    editor.stopTracking()
    editor.repaint()
  }
}

