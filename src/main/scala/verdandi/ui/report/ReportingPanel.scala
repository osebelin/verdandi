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
package verdandi.ui.report

import verdandi.model.VerdandiConfiguration
import verdandi.model.VerdandiModel
import verdandi.SummaryItem
import verdandi.ui.{ WidthStoringTable, TextResources => Text }
import javax.swing.table.AbstractTableModel
import scala.swing.{ BorderPanel, ScrollPane }
import java.util.Date

class ReportingPanel extends BorderPanel {

  object ReportTableModel extends AbstractTableModel {

    loadItems()

    var items: List[SummaryItem] = _

    val colNames = List(Text("label.costunit.id"), Text("label.costunit.name"), Text("label.duration"))

    private def loadItems() {
      def byId(one: SummaryItem, other: SummaryItem): Boolean = one.getProjectid < other.getProjectid

//      val from = VerdandiConfiguration.reportFrom
//      val to = VerdandiConfiguration.reportUntil
      // FIXME:
      val from = new Date(0)
      val to = new Date()
      items = VerdandiModel.workRecordStorage.getDurationSummaries(from, to, false).sortWith(byId)
      println("LOAD: " + items)
    }

    def durationToString(dur:Int):String = {
      val days = dur/(8*60)
      val hrs =  dur%(8*60)/60
      val mins = dur%(8*60)%60
      "%02d:%02d:%02d".format(days,hrs,mins)
    }
    
    
    def getRowCount(): Int = items.length
    def getColumnCount(): Int = 3
    def getValueAt(row: Int, col: Int): Object = {
      val si = items(row)
      col match {
        case 0 => si.getProjectid
        case 1 => si.getProjectName
        case 2 => durationToString(si.getDuration)
      }
    }
    override def getColumnClass(col: Int): Class[_] = classOf[String]

    override def getColumnName(col: Int) = colNames(col)
  }

  val reportTable = new WidthStoringTable(ReportTableModel)

  add(new ScrollPane(reportTable), BorderPanel.Position.Center)

}

