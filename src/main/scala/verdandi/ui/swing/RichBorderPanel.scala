package verdandi.ui.swing

import scala.swing.BorderPanel
import verdandi.model.DefaultListener

class RichBorderPanel extends BorderPanel with DefaultListener {
  def getPeer() = peer.asInstanceOf[javax.swing.JPanel]
}