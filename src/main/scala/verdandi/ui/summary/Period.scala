package verdandi.ui.summary

import verdandi.RichCalendar
import verdandi.RichDate
import verdandi.Predef._
import verdandi.ui.TextResources
import verdandi.CalendarMutator
import scala.collection.mutable.HashMap
import java.text.DateFormat

abstract class PeriodType {
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

}

object PeriodType {
  object CalendarWeek extends PeriodType {
    def mutator(mutator: CalendarMutator[RichCalendar]) = mutator.weekOfYear()
    protected def label = "summarypanel.periodselector.calendarweek"
    protected def getStartDate(from: RichCalendar) = from.monday
  }
  object DynWeek extends PeriodType {
    def mutator(mutator: CalendarMutator[RichCalendar]) = mutator.weekOfYear()
    protected def label = "summarypanel.periodselector.dynweek"
    protected def getStartDate(from: RichCalendar) = from.monday
  }
  object Day extends PeriodType {
    def mutator(mutator: CalendarMutator[RichCalendar]) = mutator.dayOfMonth()
    protected def label = "summarypanel.periodselector.day"
    protected def getStartDate(from: RichCalendar) = from
  }

  object Month extends PeriodType {
    def mutator(mutator: CalendarMutator[RichCalendar]) = mutator.month()
    protected def label = "summarypanel.periodselector.month"
    protected def getStartDate(from: RichCalendar) = from.zeroAll.below().dayOfMonth().set.dayOfMonth.to(1)
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

  def prefix = ""
  override def toString = prefix + formatter.format(from.getTime()) + " - " + formatter.format(RichCalendar(to).add(-1).to().dayOfMonth().getTime())

}

object Period {
  val periods = HashMap[PeriodType, Period]()
  def apply(from: PeriodType): Period = periods.getOrElseUpdate(from, from.newPeriod())

}

