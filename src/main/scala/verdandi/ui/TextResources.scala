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
package verdandi.ui

import java.util.ResourceBundle

object TextResources {

  private val rc = ResourceBundle.getBundle("verdandi/ui/TextResources");

  def apply(key: String): String = getText(key)

  def getText(key: String): String = {
    if (rc.containsKey(key))
      rc.getString(key)
    else
      key
  }

  def getText(key: String, tokens: Map[String, Any]): String = {
    var res = getText(key)
    tokens.elements.foreach((e) => { res = res.replaceAll("\\$\\{" + e._1 + "\\}", e._2.toString) })
    res
  }

}
