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
import java.awt.event.{MouseListener, MouseEvent => AWTMouseEvent}
import java.awt.{Component => AWTComponent}
import scala.swing.Publisher
import scala.swing.event.{MouseClicked, MouseEntered, MouseExited, MousePressed, MouseReleased}



trait MouseEventAdapter extends MouseListener with Publisher {
 def peer: java.awt.Component

 peer.addMouseListener(this)
 def mouseClicked(evt:AWTMouseEvent) = publish(new MouseClicked(evt))
 def mouseEntered(evt:AWTMouseEvent)= publish(new MouseEntered(evt))
 def mouseExited(evt:AWTMouseEvent)= publish(new MouseExited(evt))
 def mousePressed(evt:AWTMouseEvent)= publish(new MousePressed(evt))
 def mouseReleased(evt:AWTMouseEvent) = publish(new MouseReleased(evt))
}
