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

import scala.swing.{ BoxPanel, Component }

import javax.swing.Box.Filler
import scala.swing.Orientation
import java.awt.Dimension

class RichBoxPanel(_orientation: Orientation.Value) extends BoxPanel(_orientation) {

  def createHorizontalGlue(): Component =
    Component.wrap(new Filler(new Dimension(0, 0), new Dimension(0, 0),
      new Dimension(java.lang.Short.MAX_VALUE, 0)))

  def createVerticalGlue(): Component =
    Component.wrap(new Filler(new Dimension(0, 0), new Dimension(0, 0),
      new Dimension(0, java.lang.Short.MAX_VALUE)))
  def createGlue(): Component =
    Component.wrap(new Filler(new Dimension(0, 0), new Dimension(0, 0),
      new Dimension(java.lang.Short.MAX_VALUE, java.lang.Short.MAX_VALUE)))

  def createHorizontalStrut(width: Int): Component =
    Component.wrap(new Filler(new Dimension(width, 0), new Dimension(width, 0),
      new Dimension(width, java.lang.Short.MAX_VALUE)))
  def createVerticalStrut(height: Int): Component =
    Component.wrap(new Filler(new Dimension(0, height), new Dimension(0, height),
      new Dimension(java.lang.Short.MAX_VALUE, height)))

}

