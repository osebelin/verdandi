package verdandi.ui.summary
import verdandi.ui.swing.RichGridBagPanel
import scala.swing.GridBagPanel._
import verdandi.model.SummaryModel
import verdandi.ui.WidthStoringTable
import scala.swing.BorderPanel
import scala.swing.ScrollPane
import verdandi.ui.swing.RichBorderPanel
import scala.swing.Reactor
import verdandi.event.WorkRecordEvent
import verdandi.event.EventBroadcaster
import verdandi.ui.swing.RichBoxPanel
import scala.swing.Orientation
import scala.swing.ComboBox
import scala.swing.Action
import scala.swing.event.SelectionChanged
import verdandi.ui.swing.Spinner

class SummaryPanel extends RichBorderPanel {
  val summaryModel = new SummaryModel()
  val summaryTable = new WidthStoringTable(summaryModel)

  object PeriodSelectionPanel extends RichBoxPanel(Orientation.Horizontal) {
    val periodSelector = new ComboBox(List(PeriodType.Day, PeriodType.CalendarWeek, PeriodType.Month))
    listenTo(periodSelector.selection)
    periodSelector.selection.item = summaryModel.period.of
    contents += periodSelector
    contents += createHorizontalStrut(5)
    contents += new Spinner {
      model = summaryModel.PeriodSpinnerModel
    }
    contents += createHorizontalGlue()

    reactions += {
      case sel: SelectionChanged => summaryModel.periodChanged(Period(periodSelector.selection.item))
    }

  }

  add(new ScrollPane(summaryTable), BorderPanel.Position.Center);
  add(PeriodSelectionPanel, BorderPanel.Position.North)

}