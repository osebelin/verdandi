/**
 * *****************************************************************************
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
 * ****************************************************************************
 */
package verdandi.ui.swing

import javax.swing.ComboBoxModel
import javax.swing.AbstractListModel
import javax.swing.JComboBox
import scala.swing.ComboBox
import scala.swing.Component

class MutableComboBoxModel[A](var _items: Seq[A]) extends AbstractListModel[A] with ComboBoxModel[A] {
  private var selected = _items.lift(0).getOrElse(null)
  def getSelectedItem: AnyRef = selected.asInstanceOf[AnyRef]
  def setSelectedItem(a: Any) {
    if ((selected != null && selected != a) ||
      selected == null && a != null) {
      selected = a.asInstanceOf[A]
      fireContentsChanged(this, -1, -1)
    }
  }
  def getElementAt(n: Int) = _items(n).asInstanceOf[A]
  def getSize = _items.size

  def items = _items
  def items_=(newItems: Seq[A]) = {
    _items = newItems
    fireContentsChanged(this, -1, -1)
  }

}

// FIXME: Inherit scala.swing.ComboBox once fix for https://issues.scala-lang.org/browse/SI-3634 is released
class MutableComboBox[A](model: MutableComboBoxModel[A]) extends Component {
  override lazy val peer: JComboBox[A] = new JComboBox(model) with SuperMixin

}
