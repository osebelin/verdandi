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

import scala.swing._

abstract class VerdandiComponent extends Component {
  override lazy val peer: javax.swing.JComponent = new javax.swing.JComponent with ExtSuperMixin {}

  def getPeer = peer
  
  // we're opaque
  opaque=true

  protected trait ExtSuperMixin extends SuperMixin {
    override def setBounds(x:Int,y:Int,w:Int,h:Int ) {
      VerdandiComponent.this.setBounds(x,y,w,h)
    }
    def __super__setBounds(x:Int,y:Int,w:Int,h:Int ) {
      super.setBounds(x,y,w,h)
    }
  } 
  
  def setBounds(x:Int,y:Int,width:Int, height:Int) {
     peer match {
      case peer: ExtSuperMixin => peer.__super__setBounds(x,y,width,height)
      case _ => // it's a wrapper created on the fly
    }
  }
  
}
