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
package verdandi.ui.action;

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;

import verdandi.ui.settings.SettingsDialog;

@SuppressWarnings("serial")
public class EditSettingsAction extends AbstractAction {

  private static ResourceBundle rb = ResourceBundle.getBundle("TextResources");

  private JFrame parent;

  private SettingsDialog dialog = null;

  /**
   * @param model
   * @param parent
   */
  public EditSettingsAction(JFrame parent) {
    super(rb.getString("main.menu.edit.menuitem.settings.name"));
    this.parent = parent;
    putValue(Action.MNEMONIC_KEY, new Integer(rb.getString(
        "main.menu.edit.menuitem.settings.mnemonic").charAt(0)));
    putValue(Action.ACTION_COMMAND_KEY, "CMD_EDIT_SETTINGS");
  }

  public void actionPerformed(ActionEvent evt) {
    if (dialog == null) {
      dialog = new SettingsDialog(parent);
    }
    dialog.setVisible(true);
  }

}
