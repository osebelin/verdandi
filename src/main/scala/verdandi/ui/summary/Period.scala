package verdandi.ui.summary

import verdandi.RichCalendar
import verdandi.RichDate
import verdandi.Predef._
import verdandi.ui.TextResources
import verdandi.CalendarMutator
import scala.collection.mutable.HashMap
import java.text.DateFormat
import java.util.Date
import java.text.SimpleDateFormat

trait PeriodFormatter {
  protected val formatter = DateFormat.getDateInstance(DateFormat.MEDIUM)
  def format(from: Date, to: Date): String
}

trait RangeFormatter extends PeriodFormatter {
  def format(from: Date, to: Date): String = formatter.format(from) + " - " + formatter.format(to)
}

trait PointFormatter extends PeriodFormatter {
  def format(from: Date, to: Date): String = formatter.format(from)
}

abstract class PeriodType extends PeriodFormatter {
  protected def label: String
  override def toString = TextResources(label)
  def mutator(mutator: CalendarMutator[RichCalendar]): RichCalendar

  def newPeriod(): Period = {
    val p = new Period(this)
    p.from = getStartDate(RichCalendar().zeroAll.below().dayOfMonth())
    p.to = mutator(RichCalendar(p.from).add(1).to)
    p
  }
  protected def getStartDate(from: RichCalendar): RichCalendar
  def id = {
    val sn = getClass().getSimpleName()
    sn.substring(sn.indexOf("$") + 1, sn.lastIndexOf("$"))
  }

}

object PeriodType {
  object CalendarWeek extends PeriodType with RangeFormatter {
    def mutator(mutator: CalendarMutator[RichCalendar]) = mutator.weekOfYear()
    protected def label = "summarypanel.periodselector.calendarweek"
    protected def getStartDate(from: RichCalendar) = from.monday
  }
  object DynWeek extends PeriodType with RangeFormatter {
    def mutator(mutator: CalendarMutator[RichCalendar]) = mutator.weekOfYear()
    protected def label = "summarypanel.periodselector.dynweek"
    protected def getStartDate(from: RichCalendar) = from.monday
  }
  object Day extends PeriodType with PointFormatter {
    def mutator(mutator: CalendarMutator[RichCalendar]) = mutator.dayOfMonth()
    protected def label = "summarypanel.periodselector.day"
    protected def getStartDate(from: RichCalendar) = from
    override protected val formatter = new SimpleDateFormat("EEEE, dd.MM.yyyy")

  }
  object Month extends PeriodType with PointFormatter {
    def mutator(mutator: CalendarMutator[RichCalendar]) = mutator.month()
    protected def label = "summarypanel.periodselector.month"
    override protected val formatter = new SimpleDateFormat("MMMM yyyy")
    protected def getStartDate(from: RichCalendar) = from.zeroAll.below().dayOfMonth().set.dayOfMonth.to(1)
  }

  def apply(name: String): PeriodType = name match {
    case "CalendarWeek" => CalendarWeek
    case "DynWeek" => DynWeek
    case "Day" => Day
    case "Month" => Month
    case _ => CalendarWeek
  }

}

class Period(val of: PeriodType) {
  var from: RichCalendar = _
  var to: RichCalendar = _
  val formatter = DateFormat.getDateInstance(DateFormat.MEDIUM)
  def increment {
    from = of.mutator(from.add(1).to())
    to = of.mutator(to.add(1).to())
  }
  def decrement {
    from = of.mutator(from.add(-1).to())
    to = of.mutator(to.add(-1).to())
  }
  override def toString = of.format(from.getTime(), RichCalendar(to).add(-1).to().dayOfMonth().getTime())

}

object Period {
  val periods = HashMap[PeriodType, Period]()
  def apply(from: PeriodType): Period = periods.getOrElseUpdate(from, from.newPeriod())

}

