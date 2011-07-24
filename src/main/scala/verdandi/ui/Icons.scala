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

import java.util.ResourceBundle
import javax.swing.ImageIcon
import scala.collection.JavaConversions._
import com.weiglewilczek.slf4s.Logging

object Icons extends Logging {

  private val rc = ResourceBundle.getBundle("verdandi/ui/Icons");

  private val icons: Map[String, ImageIcon] = load()

  private def load(): Map[String, ImageIcon] = {
    var res = new scala.collection.mutable.HashMap[String, ImageIcon]()
    def load(key: String) {
      val imageURL = Thread.currentThread().getContextClassLoader().getResource(rc.getString(key))
      logger.debug("Loading key " + key + " from " + rc.getString(key) + " :=> " + imageURL)
      println(imageURL.getFile)
      //      require(imageURL.getFile.exists  )
      res += (key -> new ImageIcon(imageURL))
    }
    rc.getKeys.foreach(load(_))
    res.toMap
  }

  def getIcon(label: String) = icons(label)

  def getIconRc(key: String) = rc.getString(key)

}
