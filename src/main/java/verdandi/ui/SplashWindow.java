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
 * Created on 13.11.2006
 * Author: osebelin
 *
 */
package verdandi.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JWindow;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@SuppressWarnings("serial")
public class SplashWindow extends JWindow {

  private static final Log LOG = LogFactory.getLog(SplashWindow.class);

  private MediaTracker tracker;

  public SplashWindow(MediaTracker _tracker) {
    tracker = _tracker;
  }

  public void init() {
    URL imageURL = null;
    InputStream descrIn = getClass().getClassLoader().getResourceAsStream(
        "splash/content");
    if (descrIn != null) {
      BufferedReader br = new BufferedReader(new InputStreamReader(descrIn));
      List<String> lines = new ArrayList<String>(50);
      try {
        for (String line = br.readLine(); line != null; line = br.readLine()) {
          lines.add(line.trim());
        }
        Random rnd = new Random();
        int index = rnd.nextInt(lines.size());
        imageURL = Thread.currentThread().getContextClassLoader().getResource(
            "splash/" + lines.get(index));
      } catch (IOException e) {
        LOG.error("Cannot load random splash data", e);
      }
    } else {
      LOG.debug("No random splash manifest found.");
    }
    if (imageURL == null) {
      LOG.debug("Random splash image not found. Using fallback");
      imageURL = Thread.currentThread().getContextClassLoader().getResource(
          "splash.png");
    }

    ImageIcon icon = new ImageIcon(imageURL);
    tracker.addImage(icon.getImage(), 1);
    JLabel l = new JLabel(icon);
    getContentPane().add(l, BorderLayout.CENTER);
    pack();
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension labelSize = l.getPreferredSize();
    setLocation(screenSize.width / 2 - (labelSize.width / 2), screenSize.height
        / 2 - (labelSize.height / 2));
    setVisible(true);
    screenSize = null;
    labelSize = null;
  }

}
