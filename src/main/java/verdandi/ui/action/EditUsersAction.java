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
 * Created on 21.11.2005
 * Author: osebelin
 *
 */
package verdandi.ui.action;

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.Action;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@SuppressWarnings("serial")
public class EditUsersAction extends AbstractAction {

  private static final Log LOG = LogFactory.getLog(EditUsersAction.class);
  
  private static ResourceBundle rb = ResourceBundle.getBundle("TextResources");
  
  public EditUsersAction() {
      super(rb.getString("main.menu.edit.menuitem.users.name"));
      putValue(Action.MNEMONIC_KEY, new Integer(rb.getString("main.menu.edit.menuitem.users.mnemonic").charAt(0)));
      putValue(Action.ACTION_COMMAND_KEY, "CMD_EDIT_USERS");
      //initially disabled
      setEnabled(false);
  }

  
  /* (non-Javadoc)
   * @see javax.swing.AbstractAction#getKeys()
   */
  @Override
  public Object[] getKeys() {
    return super.getKeys();
  }


  /* (non-Javadoc)
   * @see javax.swing.AbstractAction#getValue(java.lang.String)
   */
  @Override
  public Object getValue(String arg0) {
    LOG.trace("EditUsersAction.getValue(): "+arg0);
    return super.getValue(arg0);
  }


  public void actionPerformed(ActionEvent evt) {
    LOG.debug("Edit Users action. Performed "+evt);
  }

}
