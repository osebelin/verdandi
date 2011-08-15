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

import verdandi.model.VerdandiModel
import verdandi.model.WorkRecord
import verdandi.ui.swing.{ DatePicker }
import scala.swing.{ Reactor, Publisher }
import scala.swing.event.Event
import java.util.{ Date, Calendar }
import verdandi.model.VerdandiConfiguration.WorkDayEditorConfiguration._
import com.weiglewilczek.slf4s.Logging

case class WorkDayChanged(date: Date) extends Event

class WorkDayEditorModel(private var d: Date = new Date) extends Reactor with Publisher with Logging {

  private var day = d
  private var calStart = Calendar.getInstance
  private var calEnd = Calendar.getInstance

  // otherwise it would react to its own published events
  deafTo(this)
  date = d

  reactions += {
    case (evt: DatePicker.DayChanged) => {
      date = evt.day
    }
  }

  def date = day

  def date_=(d: Date) = {
    logger.debug("Day changed to " + d)
    day = d
    calStart.setTime(d);
    calEnd.setTime(d);

    calStart.set(Calendar.MINUTE, 0)
    calStart.set(Calendar.SECOND, 0)
    calStart.set(Calendar.MILLISECOND, 0)
    calStart.set(Calendar.HOUR_OF_DAY, firstHour)

    calEnd.set(Calendar.MINUTE, 0)
    calEnd.set(Calendar.SECOND, 0)
    calEnd.set(Calendar.MILLISECOND, 0)
    calEnd.set(Calendar.HOUR_OF_DAY, lastHour)

    publish(WorkDayChanged(date))
  }

  /** @return the WorkRecords of the given day */
  def getWorkRecords(): List[WorkRecord] = {
    VerdandiModel.workRecordStorage.getWorkRecords(calStart.getTime, calEnd.getTime);
  }

}

