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

class SummaryPanel extends RichBorderPanel {
  val summaryModel = new SummaryModel()
  val summaryTable = new WidthStoringTable(summaryModel)

  object PeriodSelectionPanel extends RichBoxPanel(Orientation.Horizontal) {

    class Hallo extends Action("Hallo") {
      override def apply() {
        println("HALLO!")
      }
    }
    class Welt extends Action("Welt") {
      override def apply() {
        println("HALLO!")
      }
    }

    contents += new ComboBox(List(new Hallo, new Welt))
  }

  add(new ScrollPane(summaryTable), BorderPanel.Position.Center);
  add(PeriodSelectionPanel, BorderPanel.Position.North)

}