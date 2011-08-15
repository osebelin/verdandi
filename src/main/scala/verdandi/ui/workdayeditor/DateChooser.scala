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

import java.util.Calendar
import javax.swing.AbstractSpinnerModel
import org.jdesktop.swingx.JXDatePicker
import java.util.Date
import scala.swing.{ Orientation, Publisher }
import verdandi.ui.Icons
import verdandi.ui.swing.{ RichBoxPanel, DatePicker, Spinner }
import javax.swing.BorderFactory
import javax.swing.ImageIcon
import javax.swing.UIManager

class DateChooser(date: Date) extends RichBoxPanel(Orientation.Horizontal) with Publisher {

  def this() = this(new Date)

  UIManager.put("JXDatePicker.arrowDown.image", Icons.getIcon("icon.datepicker.select"));

  val picker = new DatePicker(date) {
    editor.setEditable(false)
  }
  val spinner = new Spinner() {
    model = new DateChooserSpinnermodel(picker)
    editor = picker
    border = BorderFactory.createEmptyBorder()
  }

  init()

  private def init() {
    contents += spinner
    deafTo(this)
    reactions += {
      case evt: DatePicker.DayChanged => {
        publish(evt)
      }
    }
    listenTo(picker)
    listenTo(spinner)
  }

}

class DateChooserSpinnermodel(val dateChooser: DatePicker) extends AbstractSpinnerModel {

  override def getNextValue(): Object = {
    val cal = Calendar.getInstance()
    cal.setTime(dateChooser.date)
    cal.add(Calendar.DAY_OF_MONTH, 1)
    cal.getTime()
  }

  override def getPreviousValue(): Object = {
    val cal = Calendar.getInstance()
    cal.setTime(dateChooser.date)
    cal.add(Calendar.DAY_OF_MONTH, -1)
    cal.getTime().asInstanceOf[Object]
  }

  override def getValue(): Object = dateChooser.date

  override def setValue(value: Object): Unit = dateChooser.date = value.asInstanceOf[Date]
}
