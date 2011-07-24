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
package verdandi.ui.settings;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;

import verdandi.model.VerdandiModel;
import verdandi.plugin.VerdandiPlugin;
import verdandi.ui.common.WidthStoringTableModel;

public class PluginTableModel extends WidthStoringTableModel {

  private static final long serialVersionUID = 1L;

  private static final ResourceBundle THEME_RC = ResourceBundle
      .getBundle("theme");

  private ImageIcon pluginEnabled, pluginDisabled;

  private List<VerdandiPlugin> plugins;

  private String columnNames[] = {
      "settingseditor.group.plugins.table.column.active.label",
      "settingseditor.group.plugins.table.column.name.label" };

  public PluginTableModel() {
    super(ResourceBundle.getBundle("TextResources"));
    plugins = VerdandiModel.getPluginLoader().getPlugins();

    URL imageURL = null;
    imageURL = Thread.currentThread().getContextClassLoader()
        .getResource(THEME_RC.getString("icon.pluginmgmt.enabled"));
    pluginEnabled = new ImageIcon(imageURL, "enabled");
    imageURL = Thread.currentThread().getContextClassLoader()
        .getResource(THEME_RC.getString("icon.pluginmgmt.disabled"));
    pluginDisabled = new ImageIcon(imageURL, "disabled");

  }

  @Override
  protected String[] getColumnNames() {
    return new String[] {
        "settingseditor.group.plugins.table.column.active.label",
        "settingseditor.group.plugins.table.column.name.label" };

  }

  @Override
  public int getRowCount() {
    return plugins.size();
  }

  @Override
  public Object getValueAt(int rowIndex, int columnIndex) {

    if (rowIndex < plugins.size()) {
      switch (columnIndex) {
      case 0:
        return pluginEnabled; // TODO
      case 1:
        return plugins.get(rowIndex).getName();
      default:
        return null;
      }
    }
    return null;
  }

  public Class<?> getColumnClass(int col) {
    switch (col) {
    case 0:
      return ImageIcon.class;
    default:
      return String.class;
    }
  }

}
