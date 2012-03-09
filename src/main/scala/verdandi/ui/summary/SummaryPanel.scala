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
import verdandi.event.SummaryPeriodChanged
import scala.swing.Label
import java.awt.Font
import verdandi.ui.TextResources
import javax.swing.BorderFactory
import java.util.prefs.Preferences
import verdandi.Prefs
import verdandi.Pref
import verdandi.StringPref
import scala.swing.GridBagPanel
import java.awt.Color

class SummaryPanel extends RichBorderPanel {
  val summaryModel = new SummaryModel()
  val summaryTable = new WidthStoringTable(summaryModel)

  object PeriodSelectionPanel extends RichGridBagPanel with Prefs {

    val c = RichGridbagConstraints.default.withInsets(2, 2, 2, 2).withFill(Fill.Vertical)

    override val prefs = Map("selectedType" -> StringPref(PeriodType.CalendarWeek.id))
    load()

    val periodSelector = new ComboBox(List(PeriodType.Day, PeriodType.CalendarWeek, PeriodType.Month)) {
      selection.item = PeriodType(prefs("selectedType").value)
      summaryModel.periodTypeChanged(selection.item)
    }
    listenTo(periodSelector.selection)

    val spinner = new Spinner {
      model = summaryModel.PeriodSpinnerModel
    }

    add(new Label(TextResources("summarypanel.periodselector.label")), c)
    add(periodSelector, c.withGridXInc)
    add(spinner, c.withGridXInc)
    add(new Label(""), c.withGridXInc.withFill(Fill.Horizontal).withWeightX(1.0))

    reactions += {
      case sel: SelectionChanged => {
        summaryModel.periodTypeChanged(periodSelector.selection.item)
      }
    }
    border = BorderFactory.createEmptyBorder(5, 0, 5, 0)

    override def storePrefs() {
      prefs("selectedType").value = periodSelector.selection.item.id
    }

  }

  object SumTotalPanel extends RichBoxPanel(Orientation.Horizontal) {
    val labelPrefix = TextResources("summarypanel.sumtotal.sumlabel")
    val sum = new Label(labelPrefix + summaryModel.sumTotal().format) {
      peer.setFont(boldFont)

      def boldFont = {
        new Font(peer.getFont.getFontName, Font.BOLD, peer.getFont.getSize);
      }

    }
    reactions += {
      case evt: SummaryPeriodChanged => sum.text = labelPrefix + summaryModel.sumTotal().format
    }
    contents += createHorizontalGlue()
    contents += sum
    border = BorderFactory.createEmptyBorder(3, 0, 3, 0)
  }

  add(new ScrollPane(summaryTable), BorderPanel.Position.Center);
  add(PeriodSelectionPanel, BorderPanel.Position.North)
  add(SumTotalPanel, BorderPanel.Position.South)

}