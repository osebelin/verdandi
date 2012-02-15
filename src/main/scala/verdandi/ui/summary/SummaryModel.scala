package verdandi.model
import verdandi.SummaryItem
import java.util.Date
import verdandi.RichCalendar
import verdandi.RichDate
import java.util.Calendar

class SummaryModel extends WidthStoringTableModel {

  override val columnNames = List(
    "summary.table.column.projectid.name",
    "summary.table.column.project.name",
    "summary.table.column.duration.name")

  var items: List[SummaryItem] = _

  def loadItems(from: Date, to: Date) {
    items = VerdandiModel.workRecordStorage.getDurationSummaries(from, to);
  }

  override def getRowCount(): Int = items.length

  def getValueAt(row: Int, col: Int): Object = {
    val si = items(row)
    col match {
      case 0 => si.getProjectid
      case 1 => si.getProjectName
      case 2 => si.formatDurationInManDays()
    }
  }

}

abstract case class Period {
  def getFrom: RichDate
  def getTo: RichDate
}

case class CurrentWeek extends Period {
  def getFrom = RichCalendar().monday.toDate
  def getTo = RichCalendar().monday.add(Calendar.DAY_OF_WEEK, 7).toDate
}
