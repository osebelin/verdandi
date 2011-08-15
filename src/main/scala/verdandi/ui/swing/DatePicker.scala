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
package verdandi.ui.swing

import javax.swing.JFormattedTextField
import scala.swing._
import org.jdesktop.swingx.{ JXDatePicker }
import java.beans.{ PropertyChangeEvent, PropertyChangeListener }
import java.util.Date
import scala.swing.event.{ Event }

class DatePicker(_date: Date) extends Component with PropertyChangeListener with Publisher {

  override lazy val peer: JXDatePicker = getDatePicker()
  def this() = this(new Date)

  private[this] def getDatePicker(): JXDatePicker = {
    val dp = new JXDatePicker
    dp.setDate(_date)
    dp.getEditor.setEditable(false)
    dp.addPropertyChangeListener(this)
    dp
  }

  def editor: JFormattedTextField = peer.getEditor
  def editor_=(_editor: JFormattedTextField) = {
    peer.setEditor(_editor)
    peer.getEditor.setEditable(false)
  }

  def date = peer.getDate
  def date_=(d: Date) = peer.setDate(d)

  /** converts the events to the scala publisher/reactor mechanism */
  def propertyChange(evt: PropertyChangeEvent) = {
    publish(DatePicker.DayChanged(peer.getDate))
  }
}

object DatePicker {
  /** Gets published when the day is changed by the DateChooser */
  case class DayChanged(day: Date) extends Event
}
