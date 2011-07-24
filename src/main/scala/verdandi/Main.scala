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
package verdandi

import verdandi.ui.InfoNodeAppWindow
import verdandi.model.{ VerdandiModel, VerdandiConfiguration => CFG }
import net.infonode.gui.laf.InfoNodeLookAndFeelThemes
import net.infonode.gui.laf.InfoNodeLookAndFeel
import javax.swing.UIManager
import com.weiglewilczek.slf4s.Logging

object Main extends Logging {

  def main(args: Array[String]) {

    UIManager
      .setLookAndFeel(new InfoNodeLookAndFeel(InfoNodeLookAndFeelThemes.getSoftGrayTheme));

    val mainWindow = new InfoNodeAppWindow()

    if ((args.length == 1 && args(0).equalsIgnoreCase("-nosplash")) || CFG.startHidden) {
      logger.info("Splash disabled");
    } else {
      mainWindow.showSplash()
    }

    try {
      mainWindow.init();
    } catch {
      case e: Throwable => logger.error("Startup error", e);
    } finally {
      mainWindow.hideSplash()
    }

  }

}
