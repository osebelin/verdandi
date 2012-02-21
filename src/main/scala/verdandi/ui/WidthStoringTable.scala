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

import javax.swing.table.TableModel
import verdandi.event.ApplicationShutdown
import verdandi.event.EventBroadcaster
import javax.swing.table.TableColumnModel
import java.util.prefs.Preferences
import scalax.swingx.SXTable
import verdandi.model.DefaultListener

class WidthStoringTable(val _model: TableModel) extends SXTable with DefaultListener {
  reactions += {
    case _: ApplicationShutdown => storeColumnWidths()
  }
  model = _model
  initColumnWidths()

  private def initColumnWidths() {
    val prefs = Preferences.userNodeForPackage(getClass())
    for (i <- 0 until model.getColumnCount) {
      val width = prefs.getInt("col." + i + ".width", columnModel.getColumn(i).getPreferredWidth)
      columnModel.getColumn(i).setPreferredWidth(width)
    }
  }

  private def storeColumnWidths() {
    val prefs = Preferences.userNodeForPackage(getClass())
    for (i <- 0 until model.getColumnCount) {
      prefs.putInt("col." + i + ".width", columnModel.getColumn(i).getWidth)
    }
  }

}
