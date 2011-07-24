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
package verdandi.ui;

import java.awt.BorderLayout;
import java.util.ResourceBundle;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JWindow;

import org.jdesktop.swingx.JXTitledPanel;

import verdandi.persistence.SaveProgressMonitor;
import verdandi.ui.common.WindowUtilities;

public class ImportProgressView extends JWindow implements SaveProgressMonitor {

  private static final long serialVersionUID = 1L;

  protected static final ResourceBundle RC = ResourceBundle
      .getBundle("TextResources");

  private JProgressBar progress;

  private JFrame parent;

  protected ImportProgressView(JFrame owner) {
    super();
    parent = owner;
    initControls();
    pack();
  }

  private void initControls() {
    JPanel p = new JPanel(new BorderLayout());
    progress = new JProgressBar(JProgressBar.HORIZONTAL, 0, 100);
    progress.setValue(0);
    progress.setStringPainted(true);
    p.add(progress, BorderLayout.CENTER);

    JXTitledPanel tp = new JXTitledPanel(RC.getString("importproject.title"), p);

    add(tp);
    pack();
    setAlwaysOnTop(true);

  }

  @Override
  public void finished() {
    setVisible(false);
  }

  @Override
  public void next(String name) {
    progress.setValue(progress.getValue() + 1);
    progress.setString(progress.getValue() + "/" + progress.getMaximum());
  }

  private void center() {
    if (parent == null) {
      WindowUtilities.centerOnScreen(this);
    } else {
      int posX = parent.getX() + (parent.getWidth() / 2) - (getWidth() / 2);
      int posY = parent.getY() + (parent.getHeight() / 2) - (getHeight() / 2);
      setBounds(posX, posY, getWidth(), getHeight());
    }
  }

  @Override
  public void start(int numRecords) {
    progress.setMaximum(numRecords);
    progress.setValue(0);
    center();
    setVisible(true);
  }

  public static void main(String[] args) {

    ImportProgressView ipv = new ImportProgressView(null);

    ipv.setVisible(true);
    ipv.start(100);
    for (int i = 0; i < 100; i++) {
      ipv.next("item " + i);
      try {
        Thread.sleep(10);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    ipv.finished();
    ipv.dispose();
    System.exit(0);
  }

}
