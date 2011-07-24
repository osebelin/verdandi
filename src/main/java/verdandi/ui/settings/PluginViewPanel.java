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

import java.awt.BorderLayout;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import verdandi.ui.action.ImportPluginAction;

public class PluginViewPanel extends SettingsPanel {

  private static final long serialVersionUID = 1L;

  private static final ResourceBundle RC = ResourceBundle
      .getBundle("TextResources");

  private PluginTable table;

  public PluginViewPanel() {
    super();
    initControls();
  }

  private void initControls() {
    setLayout(new BorderLayout());

    // TODO: Allow the unloading of modules!

    PluginTableModel tableModel = new PluginTableModel();
    table = new PluginTable(tableModel);
    JScrollPane scrl = new JScrollPane(table);
    add(scrl, BorderLayout.CENTER);

    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
    buttonPanel.add(Box.createHorizontalGlue());
    JButton importButton = new JButton(new ImportPluginAction(this));
    buttonPanel.add(importButton);
    buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    // setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
    setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    add(buttonPanel, BorderLayout.SOUTH);
  }

  @Override
  public void commit() {
    table.storeWidths();
  }

  @Override
  public void reset() {
    table.storeWidths();
  }

}
