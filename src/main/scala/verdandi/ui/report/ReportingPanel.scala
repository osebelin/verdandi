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
package verdandi.ui.report

import verdandi.model.VerdandiConfiguration
import verdandi.model.VerdandiModel
import verdandi.SummaryItem
import verdandi.ui.{ WidthStoringTable, TextResources => Text }
import javax.swing.table.AbstractTableModel
import scala.swing.{ BorderPanel, ScrollPane }
import java.util.Date
import verdandi.model.WidthStoringTableModel

class ReportingPanel extends BorderPanel {

  object ReportTableModel extends WidthStoringTableModel {

    override val columnNames = List("label.costunit.id", "label.costunit.name", "label.duration")

    loadItems()

    var items: List[SummaryItem] = _

    private def loadItems() {
      def byId(one: SummaryItem, other: SummaryItem): Boolean = one.getProjectid < other.getProjectid

      //      val from = VerdandiConfiguration.reportFrom
      //      val to = VerdandiConfiguration.reportUntil
      // FIXME:
      val from = new Date(0)
      val to = new Date()
      items = VerdandiModel.workRecordStorage.getDurationSummaries(from, to).sortWith(byId)
      println("LOAD: " + items)
    }

    def getRowCount(): Int = items.length
    def getValueAt(row: Int, col: Int): Object = {
      val si = items(row)
      col match {
        case 0 => si.getProjectid
        case 1 => si.getProjectName
        case 2 => si.formatDurationInManDays
      }
    }
    override def getColumnClass(col: Int): Class[_] = classOf[String]

  }

  val reportTable = new WidthStoringTable(ReportTableModel)

  add(new ScrollPane(reportTable), BorderPanel.Position.Center)

}

