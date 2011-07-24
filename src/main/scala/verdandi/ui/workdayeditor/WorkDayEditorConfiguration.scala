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

import verdandi.RichCalendar
object WorkDayEditorConfigurationOLD {

  def firstHour=8
  
  def lastHour=22
  
//  def minuteGranularity = VerdandiConfiguration.timeResolution
  def minuteGranularity = 15 // FIXME

  def pixelsPerSlot=12
  
  
  /** the calculated minimum height based on the configuration 
   */
  def minHeight:Int = (lastHour-firstHour)*60/minuteGranularity*pixelsPerSlot 
  
  def lowerBound = RichCalendar().hhmm(firstHour,0)
  def upperBound = RichCalendar().hhmm(lastHour,0)
  
}
