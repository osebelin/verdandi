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
 * Created on 08.12.2005
 * Author: osebelin
 *
 */
package verdandi.ui.settings;

import java.awt.BorderLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import verdandi.model.VerdandiModel;
import verdandi.plugin.PluginSettingsPanel;
import verdandi.plugin.VerdandiPlugin;
import verdandi.ui.common.OKCancelPanel;
import verdandi.ui.common.WindowUtilities;

@SuppressWarnings("serial")
public class SettingsDialog extends JDialog implements ActionListener {

  private DefaultSettingsPanel defaultSettings;

  private static ResourceBundle RC = ResourceBundle.getBundle("TextResources");

  private boolean aborted = false;

  private List<SettingsPanel> settingsPanels;

  /**
   * @param model
   * @throws HeadlessException
   */
  public SettingsDialog(JFrame parent) throws HeadlessException {
    super(parent, true);
    setTitle(RC.getString("settingseditor.title"));
    initControls();
    if (parent != null) {
      WindowUtilities.centerOnParent(this, parent);
    } else {
      WindowUtilities.centerOnScreen(this);
    }
  }

  private void initControls() {
    settingsPanels = new ArrayList<SettingsPanel>();
    getContentPane().setLayout(new BorderLayout());

    JTabbedPane tabs = new JTabbedPane(JTabbedPane.TOP);
    defaultSettings = new DefaultSettingsPanel();
    tabs.add(RC.getString("settingseditor.group.basic.title"), defaultSettings);
    settingsPanels.add(defaultSettings);

    PluginViewPanel pluginPanel = new PluginViewPanel();
    tabs.add(RC.getString("settingseditor.group.plugins.title"), pluginPanel);
    settingsPanels.add(pluginPanel);

    for (VerdandiPlugin plugin : VerdandiModel.getPluginLoader().getPlugins()) {
      PluginSettingsPanel psp = plugin.getConfigurationPanel();
      if (psp != null) {
        tabs.add(psp.getTitle(), psp);
        settingsPanels.add(psp);
      }
    }
    getContentPane().add(tabs, BorderLayout.CENTER);
    getContentPane().add(new OKCancelPanel(this), BorderLayout.SOUTH);
    pack();
  }

  public void actionPerformed(ActionEvent evt) {
    if (evt.getActionCommand().equals(OKCancelPanel.CMD_OK)) {
      for (SettingsPanel settingsPanel : settingsPanels) {
        settingsPanel.commit();
      }
    } else {
      for (SettingsPanel settingsPanel : settingsPanels) {
        settingsPanel.reset();
      }
      aborted = true;
    }
    setVisible(false);
  }

  public boolean isAborted() {
    return aborted;
  }

}
