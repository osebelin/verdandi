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

class SummaryModel extends WidthStoringTableModel {

  override val columnNames = List(
    "label.costunit.id",
    "label.costunit.name",
    "label.duration")

  var items: List[SummaryItem] = _

  var period: Period = FixedWeek()

  loadItems()

  reactions += {
    case e: WorkRecordEvent => {
      loadItems()
      fireTableDataChanged();
    }
  }

  def loadItems() {
    items = VerdandiModel.workRecordStorage.getDurationSummaries(period.from.toDate(), period.to.toDate());
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
  def from: RichCalendar
  def to: RichCalendar
}

case class FixedWeek extends Period {
  def from = RichCalendar().monday
  def to = RichCalendar().monday.add(7).to().dayOfWeek()

}

case class Month extends Period {
  def from = RichCalendar().set.dayOfMonth.to(0)
  def to = RichCalendar().set.dayOfMonth.to(0)
}

