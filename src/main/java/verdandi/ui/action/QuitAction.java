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

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;

public class QuitAction extends AbstractAction {

  private static final long serialVersionUID = 1L;

  private static final ResourceBundle RC = ResourceBundle
      .getBundle("TextResources");

  public QuitAction() {
    super(RC.getString("main.menuitem.quit.name"));
    putValue(Action.MNEMONIC_KEY, new Integer(RC.getString(
        "main.menuitem.quit.mnemonic").charAt(0)));
    putValue(Action.ACTION_COMMAND_KEY, "CMD_QUIT");
    putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_Q,
        ActionEvent.CTRL_MASK));
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    // model.shutdown();
    System.exit(0);
  }

}
