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

class SummaryPanel extends RichBorderPanel {

  val summaryModel = new SummaryModel()
  val summaryTable = new WidthStoringTable(summaryModel)

  add(new ScrollPane(summaryTable), BorderPanel.Position.Center);

}