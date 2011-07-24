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

import scala.swing.{ Component, MenuItem }
import javax.swing.JPopupMenu
import java.awt.{ Point, Component => AWTComponent }

class PopupMenu extends Component {
  override lazy val peer = new JPopupMenu with SuperMixin
  def add(item: MenuItem): Unit = peer.add(item.peer)
  def show(invoker: Component, x: Int, y: Int): Unit = peer.show(invoker.peer, x, y)

  override def location = peer.getLocation
  def location_=(loc: Point) = peer.setLocation(loc)

  def invoker = peer.getInvoker
  def invoker_=(_invoker: Component) = peer.setInvoker(_invoker.peer)
}
