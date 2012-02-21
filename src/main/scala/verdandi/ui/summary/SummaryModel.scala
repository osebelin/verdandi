package verdandi.model
import verdandi.SummaryItem
import java.util.Date
import verdandi.RichCalendar
import verdandi.RichDate
import verdandi.Predef._
import java.util.Calendar
import scala.swing.Reactor
import verdandi.event.WorkRecordEvent
import verdandi.event.EventBroadcaster

class SummaryModel extends WidthStoringTableModel {

  override val columnNames = List(
    "label.costunit.id",
    "label.costunit.name",
    "label.duration")

  var items: List[SummaryItem] = _

  var period: Period = CurrentWeek()

  loadItems()

  reactions += {
    case e: WorkRecordEvent => {
      loadItems()
      fireTableDataChanged();
    }
  }

  def loadItems() {
    items = VerdandiModel.workRecordStorage.getDurationSummaries(period.from, period.to);
  }

  override def getRowCount(): Int = items.length

  def getValueAt(row: Int, col: Int): Object = {
    val rec = items(row)
    col match {
      case 0 => rec.getProjectid
      case 1 => rec.getProjectName
      case 2 => rec.formatDurationInManHours()
    }
  }
}

abstract case class Period {
  def from: RichDate
  def to: RichDate
}

case class CurrentWeek extends Period {
  def from = RichCalendar().monday.toDate
  def to = RichCalendar().monday.add(Calendar.DAY_OF_WEEK, 7).toDate
}
