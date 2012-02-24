package verdandi.ui.summary

import verdandi.RichCalendar
import verdandi.RichDate
import verdandi.Predef._
import verdandi.ui.TextResources

abstract class Period {
  def from: RichCalendar
  def to: RichCalendar
  protected def label: String
  override def toString = TextResources(label)
}

object Period {
  object CalendarWeek extends Period {
    def from = RichCalendar().monday
    def to = RichCalendar().monday.add(7).to().dayOfWeek()
    protected def label = "summarypanel.periodselector.calendarweek"
  }

  object Day extends Period {
    def from = RichCalendar().zeroAll.below().dayOfWeek()
    def to = RichCalendar().zeroAll.below().dayOfWeek().add(1).to.dayOfMonth()
    protected def label = "summarypanel.periodselector.day"
  }
  object DynWeek extends Period {
    def from = RichCalendar().monday
    def to = RichCalendar().monday.add(7).to().dayOfWeek()
    protected def label = "summarypanel.periodselector.dynweek"
  }

  object Month extends Period {
    def from = RichCalendar().zeroAll.below().dayOfMonth().set.dayOfMonth.to(1)
    def to = RichCalendar().zeroAll.below().dayOfMonth().set.dayOfMonth.to(1).add(1).to.month()
    protected def label = "summarypanel.periodselector.month"
  }
}

