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

import scala.swing.{Component,Menu,MenuItem, Action}
import javax.swing.ImageIcon

class DynamicMenu(_title:String) extends Menu(_title){
  def this(_icon:ImageIcon) = {
    this("")
    icon=_icon
    this
  }
  def add(i:MenuItem) = {contents+=i}
  def add(a:Action) = contents += new MenuItem(a)
  def clear() = contents.clear()
}

