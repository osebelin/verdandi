/*******************************************************************************
 * Copyright 2010 Olaf Sebelin
 * 
 * This file is part of Verdandi.
 * 
 * Verdandi is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Verdandi is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Verdandi.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package verdandi.ui

import verdandi.ui.swing.RichBoxPanel
import scala.swing.{ Orientation, Button }
import javax.swing.{ Box, BorderFactory }
import scala.swing.Action

class OKCancelPanel(val onCommit: (() => Unit),
                    val onCancel: (() => Unit),
                    val commitLabel: String = TextResources("okcancel.ok"),
                    val cancelLabel: String = TextResources("okcancel.cancel")) extends RichBoxPanel(Orientation.Horizontal) {

  val okButton = new Button() {
    action = new Action(commitLabel) { def apply() = onCommit() }
  }
  val cancelButton = new Button() {
    action = new Action(cancelLabel) { def apply() = onCancel() }
  }
  contents += createHorizontalGlue
  contents += okButton
  contents += createHorizontalStrut(5)
  contents += cancelButton
  contents += createHorizontalStrut(5)
  border = BorderFactory.createEmptyBorder(2, 2, 2, 2)

}
