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
package verdandi.ui.action;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import verdandi.model.VerdandiModel;

public class ImportPluginAction extends VerdandiAction {

  private static final Log LOG = LogFactory.getLog(ImportPluginAction.class);

  private static final String KEY_CWD = "cwd";

  private Component parent;

  public ImportPluginAction(Component parent) {
    super("pluginimportaction.name");
    this.parent = parent;
  }

  private static final long serialVersionUID = 1L;

  /**
   * Ask the import file from the user
   */
  private File getFile() {
    File res = null;
    Preferences prefs = Preferences.userNodeForPackage(getClass());
    File importDir = new File(prefs.get(KEY_CWD, System
        .getProperty("user.home")));

    JFileChooser chooser = new JFileChooser(importDir);
    chooser.setFileFilter(new FileFilter() {

      @Override
      public boolean accept(File f) {
        return f.getName().toLowerCase().endsWith(".jar") || f.isDirectory();
      }

      @Override
      public String getDescription() {
        return RC
            .getString("pluginimportaction.filechooser.jarfilter.description");
      }
    });

    chooser
        .setDialogTitle(RC.getString("pluginimportaction.filechooser.title"));
    chooser.setMultiSelectionEnabled(false);

    if (chooser.showOpenDialog(parent) != JFileChooser.APPROVE_OPTION) {
      LOG.debug("User cancelled");
      return null;
    }
    res = chooser.getSelectedFile();

    prefs.put("import.dir", res.getParent());
    try {
      prefs.flush();
    } catch (BackingStoreException e) {
      LOG.error("Cannot write export file preference", e);
    }

    return res;
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    File fileToLoad = getFile();

    if (fileToLoad == null) {
      return;
    }

    File destFile = new File(VerdandiModel.getConf().getPluginDir(), fileToLoad
        .getName());
    // FIXME: overwrite won't work under windows!
    FileInputStream fIn = null;
    FileOutputStream fOut = null;
    try {
      fIn = new FileInputStream(fileToLoad);
      fOut = new FileOutputStream(destFile);
      byte buf[] = new byte[4096];
      for (int read = fIn.read(buf); read != -1; read = fIn.read(buf)) {
        fOut.write(buf, 0, read);
      }
    } catch (IOException e) {
      // VerdandiException ve = new VerdandiException()
      // model.fireEvent(new ErrorEvent())
      LOG.error("", e);
    } finally {
      if (fIn != null) {
        try {
          fIn.close();
        } catch (Throwable t) {
          LOG.error("Cannot close stream: ", t);
        }
      }
      if (fOut != null) {
        try {
          fOut.close();
        } catch (Throwable t) {
          LOG.error("Cannot close stream: ", t);
        }
      }
    }

    JOptionPane.showMessageDialog(parent, RC
        .getString("pluginimportaction.info.message"), RC
        .getString("pluginimportaction.info.title"),
        JOptionPane.INFORMATION_MESSAGE);

  }

}
