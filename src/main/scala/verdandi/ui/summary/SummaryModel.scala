package verdandi.model
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
import javax.swing.SpinnerModel
import javax.swing.AbstractSpinnerModel
import verdandi.ui.summary.PeriodType
import scala.swing.Publisher
import scala.swing.event.ValueChanged
import verdandi.event.VerdandiEvent
import verdandi.event.SummaryPeriodChanged
import verdandi.event.SummaryPeriodTypeChanged
import verdandi.Duration

class SummaryModel extends WidthStoringTableModel {

  override val columnNames = List(
    "label.costunit.id",
    "label.costunit.name",
    "label.duration")

  var items: List[SummaryItem] = _

  var period: Period = Period(PeriodType.CalendarWeek)

  loadItems()

  reactions += {
    case e: WorkRecordEvent => {
      loadItems()
    }
  }

  def periodTypeChanged(pt: PeriodType) {
    period = Period(pt)
    loadItems()
    EventBroadcaster.publish(SummaryPeriodTypeChanged(pt))
  }

  def loadItems() {
    items = VerdandiModel.workRecordStorage.getDurationSummaries(period.from.toDate(), period.to.toDate())
    fireTableDataChanged();
    EventBroadcaster.publish(SummaryPeriodChanged(period))
  }

  def sumTotal(): Duration = Duration(items.foldLeft(0)((sum, item) => item.duration + sum))

  override def getRowCount(): Int = items.length

  def getValueAt(row: Int, col: Int): Object = {
    val rec = items(row)
    col match {
      case 0 => rec.projectid
      case 1 => rec.projectName
      case 2 => rec.formatDurationInManHours()
    }
  }

  object PeriodSpinnerModel extends AbstractSpinnerModel with Reactor {

    listenTo(EventBroadcaster)

    reactions += {
      case evt: SummaryPeriodTypeChanged => periodTypeChanged()
    }

    def periodTypeChanged() {
      println(">>> periodTypeChanged()")
      //periodChanged()
      fireStateChanged()
    }

    def periodChanged() {
      fireStateChanged()
      loadItems()
    }

    override def getNextValue(): Object = {
      period.increment
      period
    }

    override def getPreviousValue(): Object = {
      period.decrement
      period
    }

    override def getValue(): Object = period

    // Change was a side effect of getNextValue
    override def setValue(value: Object): Unit = periodChanged()

  }

}

