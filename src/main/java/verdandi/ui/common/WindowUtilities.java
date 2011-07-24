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
/*
 * Created on 12.11.2005
 * Author: osebelin
 *
 */
package verdandi.ui.common;

import java.awt.Component;
import java.awt.Toolkit;

import javax.swing.JDialog;
import javax.swing.JFrame;

public class WindowUtilities {

  /**
   * Centers a component on the screen
   * @param c
   */
  public static void centerOnScreen(Component c) {
    int posX = (Toolkit.getDefaultToolkit().getScreenSize().width / 2)
        - (c.getWidth() / 2);
    int posY = (Toolkit.getDefaultToolkit().getScreenSize().height / 2)
        - (c.getHeight() / 2);
    c.setBounds(posX, posY, c.getWidth(), c.getHeight());
  }
  
  /**
   * Centers a Dialog on its parent frame;
   * 
   * @param child
   * @param parent
   */
  public static void centerOnParent(JDialog child, JFrame parent) {
    
    int posX = parent.getX() + (parent.getWidth()/2) - (child.getWidth()/2);
    int posY = parent.getY() + (parent.getHeight()/2)  - (child.getHeight()/2);
    child.setBounds(posX, posY, child.getWidth(), child.getHeight());
  }
  
  
}
