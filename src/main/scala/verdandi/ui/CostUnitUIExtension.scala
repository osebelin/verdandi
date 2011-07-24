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


import verdandi.model._
import scala.swing.{MenuItem,Action}



trait CostUnitUIAdpater {
  
  /** Build a List of MenuItems from a List of CostUnits 
   * Allows */
  def getCostUnitSelector(xxs:List[CostUnit], cb:(CostUnit => Action) ): List[MenuItem];
  
}


object StandardCostUnitUIAdpater extends CostUnitUIAdpater {
  
  def getCostUnitSelector(xxs:List[CostUnit], cb:(CostUnit => Action) ): List[MenuItem] = xxs match {
    case List() => Nil 
    case x::xs => new MenuItem(cb(x)) :: getCostUnitSelector(xs,cb)
  }
}


object CostUnitUIExtension {
  
  private val std = StandardCostUnitUIAdpater
  
  def adapter:CostUnitUIAdpater = std
}
