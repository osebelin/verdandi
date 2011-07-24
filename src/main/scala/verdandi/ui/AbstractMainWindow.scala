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

import verdandi.model.VerdandiConfiguration
import scala.swing.Frame
import java.awt.MediaTracker
import com.weiglewilczek.slf4s.Logging

abstract class AbstractMainWindow extends Frame with Logging {

  title = "Verdandi"

  iconImage = Icons.getIcon("verdandi.icon").getImage

  protected var splash: SplashWindow = _

  val systray = new VerdandiSystemTray(this.peer)

  def init()

  def showSplash() {
    val tracker = new MediaTracker(this.peer);
    splash = new SplashWindow(tracker);
    splash.init();
    tracker.waitForAll();
    splash.setVisible(true);
  }

  def hideSplash() {
    if (splash != null) {
      splash.setVisible(false);
      splash.dispose();
      splash = null;
    }
  }

  override def closeOperation() {
    logger.debug("CLOSE OPN: " + VerdandiConfiguration.exitOnClose)
    if (VerdandiConfiguration.exitOnClose) System.exit(0)
    else visible = false
  }

}
