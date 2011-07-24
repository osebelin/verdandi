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
package verdandi.ui.swing

import scala.swing.Component
import scala.swing.GridBagPanel
import java.awt.Insets

class RichGridBagPanel extends GridBagPanel {
  class RichGridbagConstraints extends Constraints {
    import scala.swing.GridBagPanel._
    def having[T](f: T => Unit, value: T): RichGridbagConstraints = {
      f(value)
      this
    }
    def withGridX(x: Int) = having(gridx_=, x)
    def withGridY(y: Int) = having(gridy_=, y)
    def withGridXInc = having(gridx_=, gridx + 1)
    def withGridYInc = having(gridy_=, gridy + 1)
    def withGridXDec = having(gridx_=, gridx - 1)
    def withGridYDec = having(gridy_=, gridy - 1)
    def withGrid(x: Int, y: Int) = having(gridx_=, x).having(gridy_=, y)
    def withGridWidth(width: Int) = having(gridwidth_=, width)
    def withGridHeight(height: Int) = having(gridheight_=, height)
    def withWeightX(weight: Double) = having(weightx_=, weight)
    def withWeightY(weight: Double) = having(weighty_=, weight)
    def withAnchor(_anchor: Anchor.Value) = having(anchor_=, _anchor)
    def withFill(_fill: Fill.Value) = having(fill_=, _fill)
    def withInsets(_insets: Insets) = having(insets_=, _insets)
    def withInsets(top: Int, left: Int, bottom: Int, right: Int) = having(insets_=, new Insets(top, left, bottom, right))
    def withIpaX(x: Int) = having(ipadx_=, x)
    def withIpadY(y: Int) = having(ipady_=, y)
  }

  override protected def add(c: Component, l: Constraints) {
    peer.add(c.peer, l.peer)
  }
}
