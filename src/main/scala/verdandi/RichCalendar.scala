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
package verdandi

import java.util.{ Calendar, Date }

class RichCalendar(val cal:Calendar) extends Ordered[Calendar] {
  
  override def compare(other:Calendar):Int = cal.compareTo(other)
  
  val zeroableFields = List(Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.SECOND, Calendar.MILLISECOND)

  def zeroBelow(field:Int): RichCalendar = {
    zeroableFields.filter( _ > field).foreach(x=> cal.set(x,0))
    this
  }
  
  def hour(h: Int): RichCalendar = {
    cal.set(Calendar.HOUR_OF_DAY, h)
    this
  }

  def minute(m: Int): RichCalendar = {
    cal.set(Calendar.MINUTE, m)
    this
  }

  def addMinute(m: Int): RichCalendar = {
    cal.add(Calendar.MINUTE, m)
    this
  }
  
  def hhmm(hour:Int,minute:Int): RichCalendar = {
    cal.set(Calendar.HOUR_OF_DAY, hour)
    cal.set(Calendar.MINUTE, minute)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)
    this
  }
  
  def toDate():RichDate = new RichDate(cal.getTime)
  
  
}

object RichCalendar {

  def apply(): RichCalendar = new RichCalendar(Calendar.getInstance)

  def apply(date: Date): RichCalendar = {
    val res = new RichCalendar(Calendar.getInstance)
    res.cal.setTime(date)
    res
  }
  def apply(cal:Calendar): RichCalendar = new RichCalendar(cal)
  
  def apply(date: Date, toZeroBelow: Int): RichCalendar = {
    val res = new RichCalendar(Calendar.getInstance)
    res.cal.setTime(date)
    res
  }

}
