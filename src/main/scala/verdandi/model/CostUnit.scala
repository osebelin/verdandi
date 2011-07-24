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
package verdandi.model

import scala.reflect.{ BeanProperty, BooleanBeanProperty }

@serializable
@SerialVersionUID(1)
class CostUnit(@BeanProperty var id: String, @BeanProperty var name: String, @BeanProperty var description: String) {
  // FIXME: Make id a val.

  // FIXME: Get rid of this
  def this() = this(null, null, null)

  @BooleanBeanProperty
  var active = true

  // FIXME: Suspicious. There should be a specicifc sublass in the editor model!
  @BooleanBeanProperty
  var newProject = false

  override def toString(): String = name + " (" + id + ")"

  /**
   * {@inheritDoc}
   */
  override def hashCode(): Int = (if (id == null) 0 else id.hashCode)

  /**
   * {@inheritDoc}
   */
  override def equals(other: Any): Boolean =  other match {
    case that:CostUnit => this.id ==that.id 
    case _ => false
  }
}

object CostUnit {
  /** Sorts a List by name */
  def byNameSorter(a: CostUnit, b: CostUnit): Boolean = a.name < b.name
}
