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
 * Created on 12.03.2005
 * Author: osebelin
 *
 */
package verdandi.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

@SuppressWarnings("serial")
public class ErrorDialog extends JDialog implements ActionListener {

	public static final String CMD_DETAILS_SHOW="show_details";
	public static final String CMD_DETAILS_HIDE="hide_details";

	public static final String LABEL_DETAILS_SHOW="<<< Details";
	public static final String LABEL_DETAILS_HIDE=">>> Details ";

	
	
	private String message;
	private Throwable cause;
	private JButton btnDetails;
	private JTextArea messageField, causeField; 
	
	
	/**
	 * @param message
	 * @param cause
	 */
	public ErrorDialog(JFrame parent, String message, Throwable cause) {
		super(parent, "Fehler", true);
		this.message = message;
		this.cause = cause;
		initControls();
	}
	
	private void initControls() {
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(5,5,5,5);
		
		c.gridx=c.gridy=0;
		c.gridwidth=2;
		c.fill=GridBagConstraints.HORIZONTAL;
		c.anchor=GridBagConstraints.NORTHWEST;
		c.weightx=1.0;
		c.weighty=1.0;
		
		messageField = new JTextArea(message);
		messageField.setEditable(false);
		messageField.setBorder(BorderFactory.createEmptyBorder());
		
		
		panel.add(messageField, c);
		
		c.gridy=c.gridx=1;
		c.gridwidth=1;
		c.fill=GridBagConstraints.NONE;
		c.anchor=GridBagConstraints.NORTHEAST;
		
		btnDetails = new JButton(LABEL_DETAILS_SHOW);
		btnDetails.setActionCommand(CMD_DETAILS_SHOW);
		btnDetails.addActionListener(this);

		panel.add(btnDetails,c);
		
		c.gridx=0;
		c.gridy=2;
		c.gridwidth=2;
		c.fill=GridBagConstraints.BOTH;
		c.anchor=GridBagConstraints.NORTH;
		
		
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		cause.printStackTrace(pw);
		causeField = new JTextArea(sw.toString());
		causeField.setVisible(false);
		panel.add(causeField,c);

		panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		
		this.getContentPane().add(panel, BorderLayout.CENTER);
		
		pack();
	}

	public void actionPerformed(ActionEvent evt) {
		if (evt.getActionCommand().equals(CMD_DETAILS_HIDE)) {
			btnDetails.setActionCommand(CMD_DETAILS_SHOW);
			causeField.setVisible(false);
			pack();
		} else if (evt.getActionCommand().equals(CMD_DETAILS_SHOW)) {
				btnDetails.setActionCommand(CMD_DETAILS_HIDE);
				causeField.setVisible(true);
				pack();
		}
		
	}

	/* (non-Javadoc)
	 * @see java.awt.Container#getMinimumSize()
	 */
	@Override
  public Dimension getMinimumSize() {
		return new Dimension(300,100);
	}

	/* (non-Javadoc)
	 * @see java.awt.Container#getPreferredSize()
	 */
	@Override
  public Dimension getPreferredSize() {
		return getMinimumSize();
	}
	
	
	
	
}
