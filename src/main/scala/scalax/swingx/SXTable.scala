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
package scalax.swingx

import javax.swing.table.TableColumnModel
import scala.swing.Table
import org.jdesktop.swingx.JXTable
import scala.swing.Scrollable
import scala.swing.Component

class SXTable extends Table {
  override lazy val peer: JXTable = new JXTable()

  def columnModel = peer.getColumnModel
  def columnModel_=(tcm: TableColumnModel) = peer.setColumnModel(tcm)

}
