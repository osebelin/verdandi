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
package verdandi

import java.util.{ Calendar, Date }
import java.util.Calendar._

class RichCalendar(val cal: Calendar) extends Ordered[Calendar] {

  cal.set(SECOND, 0)
  cal.set(MILLISECOND, 0)

  override def compare(other: Calendar): Int = cal.compareTo(other)

  val zeroableFields = List(MONTH, DAY_OF_MONTH, HOUR_OF_DAY, MINUTE, SECOND, MILLISECOND)

  def zeroBelow(field: Int): RichCalendar = {
    zeroableFields.filter(_ > field).foreach(x => cal.set(x, 0))
    this
  }

  def hour: Int = cal.get(HOUR_OF_DAY)
  def hour(h: Int): RichCalendar = {
    cal.set(HOUR_OF_DAY, h)
    this
  }

  def minute: Int = cal.get(MINUTE)

  def minute(m: Int): RichCalendar = {
    cal.set(Calendar.MINUTE, m)
    this
  }

  def addMinute(m: Int): RichCalendar = {
    cal.add(Calendar.MINUTE, m)
    this
  }

  def hhmm(hour: Int, minute: Int): RichCalendar = {
    cal.set(HOUR_OF_DAY, hour)
    cal.set(MINUTE, minute)

    this
  }

  def toDate(): RichDate = new RichDate(cal.getTime)

  def monday(): RichCalendar = set.dayOfWeek().to(MONDAY)

  def set: SetCalendarValue = SetCalendarValue(this)

  def add(value: Int) = AddDestination(this, value)

  def zeroAll = ZeroAll(this)

  override def toString: String = cal.getTime().toString

}

abstract case class CalendarMutator[A](dest: RichCalendar) {
  def opn(field: Int): A
  def dayOfMonth(): A = opn(Calendar.DAY_OF_MONTH)
  def dayOfWeek(): A = opn(Calendar.DAY_OF_WEEK)
  def weekOfYear(): A = opn(Calendar.WEEK_OF_YEAR)
  def month(): A = opn(Calendar.MONTH)
  def hour(): A = opn(Calendar.HOUR_OF_DAY)
  def minute(): A = opn(Calendar.MINUTE)
}

case class SetCalendarValue(_dest: RichCalendar) extends CalendarMutator[SetOpn](_dest) {
  def opn(field: Int) = new SetOpn(dest, field)
}
class SetOpn(dest: RichCalendar, field: Int) {
  def to(value: Int): RichCalendar = {
    dest.cal.set(field, value)
    dest
  }
}
case class AddToCalendarValue(_dest: RichCalendar, value: Int) extends CalendarMutator[RichCalendar](_dest) {
  def opn(field: Int): RichCalendar = {
    dest.cal.add(field, value)
    dest
  }
}

case class AddDestination(dest: RichCalendar, value: Int) {
  def to(): CalendarMutator[RichCalendar] = new AddToCalendarValue(dest, value)
}

case class ZeroAll(dest: RichCalendar) {
  case class ZeroAllBelow(_dest: RichCalendar) extends CalendarMutator[RichCalendar](_dest) {
    def opn(field: Int): RichCalendar = {
      dest.zeroableFields.filter(_ > field).foreach(x => {
        dest.cal.set(x, 0)
        println(":" + x)
      })
      dest
    }
  }
  def below(): CalendarMutator[RichCalendar] = ZeroAllBelow(dest)
}

object RichCalendar {

  def apply(): RichCalendar = new RichCalendar(Calendar.getInstance)

  def apply(date: Date): RichCalendar = {
    val res = new RichCalendar(Calendar.getInstance)
    res.cal.setTime(date)
    res
  }
  def apply(cal: Calendar): RichCalendar = new RichCalendar(cal)

  def apply(from: RichCalendar): RichCalendar = new RichCalendar(from.cal.clone().asInstanceOf[Calendar])

}
