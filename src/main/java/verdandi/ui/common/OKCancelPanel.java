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
 * Created on 24.02.2004
 * Author: osebelin
 *
 */
package verdandi.ui.common;

import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * TODO: Eigenen DIalog abuen, der willApprove und willcancelled kann!
 */
public class OKCancelPanel extends JPanel {

  private static final long serialVersionUID = 1L;

  public static final String CMD_OK = "CMD_OK";

  public static final String CMD_CANCEL = "CMD_CANCEL";

  private JButton btnOK = new JButton("OK");

  private JButton btnCancel = new JButton("Abbrechen");

  private ActionListener parent = null;

  /**
   * 
   */
  public OKCancelPanel(ActionListener listener) {
    super();
    parent = listener;
    initControls();
  }

  private void initControls() {
    btnOK.setActionCommand(CMD_OK);
    btnCancel.setActionCommand(CMD_CANCEL);
    btnOK.addActionListener(this.parent);
    btnCancel.addActionListener(this.parent);
    btnOK.setMnemonic(KeyEvent.VK_O);
    btnCancel.setMnemonic(KeyEvent.VK_R);
    this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
    this.add(Box.createHorizontalGlue());
    this.add(btnOK);
    this.add(Box.createHorizontalStrut(5));
    this.add(btnCancel);
    this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
  }

  /**
   * @return
   */
  public JButton getBtnCancel() {
    return btnCancel;
  }

  /**
   * @return
   */
  public JButton getBtnOK() {
    return btnOK;
  }

}
