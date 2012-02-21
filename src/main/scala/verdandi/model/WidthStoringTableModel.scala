package verdandi.model
import javax.swing.table.AbstractTableModel
import verdandi.ui.TextResources

abstract class WidthStoringTableModel extends AbstractTableModel with DefaultListener {
  val columnNames: List[String];
  override def getColumnName(col: Int): String = TextResources(columnNames(col))
  override def getColumnCount(): Int = columnNames.length

}