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
import verdandi.Predef._
import verdandi.ui.TextResources
import verdandi.ui.summary.Period

class SummaryModel extends WidthStoringTableModel {

  override val columnNames = List(
    "label.costunit.id",
    "label.costunit.name",
    "label.duration")

  var items: List[SummaryItem] = _

  var period: Period = Period.CalendarWeek

  loadItems()

  reactions += {
    case e: WorkRecordEvent => {
      loadItems()
    }
  }

  def periodChanged(p: Period) {
    period = p
    loadItems()
  }

  def loadItems() {
    items = VerdandiModel.workRecordStorage.getDurationSummaries(period.from.toDate(), period.to.toDate())
    fireTableDataChanged();
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

