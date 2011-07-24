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
 * Created on 29.10.2006
 * Author: osebelin
 *
 */
package verdandi.ui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ResourceBundle;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import verdandi.model.CostUnit;

/**
 *
 */
@SuppressWarnings("serial")
public class ProjectEditorPanel extends JPanel{

  private static ResourceBundle rb = ResourceBundle.getBundle("TextResources");
  
  private CostUnit project;

  private JTextField txtId, txtName;
  
  private JTextArea  txtDescription;
  
  private JCheckBox chkActive;
  
  private JLabel msgLabel;
  
  /**
   * @param project
   */
  public ProjectEditorPanel(CostUnit project) {
    super();
    this.project = project;
    initControls();
  }

  private void initControls() {

    setLayout(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5,5,5,5);
  
    gbc.gridx=0;
    gbc.gridy=0;
    gbc.gridwidth=2;
    
    msgLabel = new JLabel(" ");
    gbc.anchor=GridBagConstraints.CENTER;
    add(msgLabel, gbc);
    
    

    gbc.anchor=GridBagConstraints.WEST;
    gbc.gridwidth=1;
    gbc.gridx=0;
    gbc.gridy=1;
    gbc.weightx=0.0;
    gbc.weighty=0.0;
    gbc.fill=GridBagConstraints.NONE;
    add(new JLabel(rb.getString("projecteditor.label.id")), gbc);
    gbc.gridy++;
    add(new JLabel(rb.getString("projecteditor.label.name")), gbc);
    gbc.gridy++;
    add(new JLabel(rb.getString("projecteditor.label.desc")), gbc);
    gbc.gridy++;
    add(new JLabel(rb.getString("projecteditor.label.activestate")), gbc);
    gbc.gridy++;

    
    gbc.gridx=1;
    gbc.gridy=1;
    gbc.weightx=1;
    gbc.fill=GridBagConstraints.HORIZONTAL;
    
    txtId = new JTextField(project.getId());
    if (!project.isNewProject()) {
      txtId.setEditable(false);
    }
    add(txtId, gbc);
    gbc.gridy++;
    
    txtName = new JTextField(project.getName());
    add(txtName, gbc);
    gbc.gridy++;
    
    gbc.fill=GridBagConstraints.BOTH;
    gbc.weighty=1;
    txtDescription = new JTextArea(4,40);
    txtDescription.setText(project.getDescription());
    JScrollPane scrl = new JScrollPane(txtDescription, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    add(scrl, gbc);
    gbc.gridy++;
    
    gbc.weighty=0.0;
    gbc.fill=GridBagConstraints.HORIZONTAL;
    
    chkActive = new JCheckBox(rb.getString("projecteditor.label.active"));
    chkActive.setSelected(project.isActive());
    add(chkActive,gbc);
    
  }
  
  /**
   * Checks if the entries are valid. 
   * 
   * @return
   */
  boolean validateEntries() {
    if (txtId.getText()==null || txtId.getText().equals("")) {
      msgLabel.setForeground(Color.RED);
      msgLabel.setText(rb.getString("projecteditor.errmsg.nopid"));
      return false;
    }
    
    if (txtName.getText()==null || txtName.getText().equals("")) {
      msgLabel.setForeground(Color.RED);
      msgLabel.setText(rb.getString("projecteditor.errmsg.noname"));
      return false;
    }
    msgLabel.setText(" ");
    msgLabel.setForeground(Color.BLACK);
    
    return true;
  }
  
  /**
   * Does NOT check validity!
   */
  void updateProject() {
    if (project.isNewProject()) {
      project.setId(txtId.getText());
    } 
    project.setName(txtName.getText());
    project.setDescription(txtDescription.getText());
    project.setActive(chkActive.isSelected());
  }
  
  void resetProject() {
    boolean wasNew = project.isNewProject();
    project = new CostUnit();
    project.setNewProject(wasNew);
  }
  
  
}
