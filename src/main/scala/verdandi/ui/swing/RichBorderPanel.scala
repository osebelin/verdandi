package verdandi.ui.swing

import scala.swing.BorderPanel

class RichBorderPanel extends BorderPanel {
  def getPeer() = peer.asInstanceOf[javax.swing.JPanel]
}