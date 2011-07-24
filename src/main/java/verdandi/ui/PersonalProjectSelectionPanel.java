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
package verdandi.ui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import verdandi.model.PersonalProjectSelectionTableModel;

public class PersonalProjectSelectionPanel extends ProjectViewingPanel
    implements ActionListener, DocumentListener {

  private static final long serialVersionUID = 1L;

  private static final String CMD_RELOAD = "reload";

  private PersonalProjectSelectionTableModel personalProjectsModel;

  private PersonalProjectSelectionTable table;

  public PersonalProjectSelectionPanel() {
    super();
    initControls();
  }

  private void initControls() {
    setLayout(new BorderLayout());
    personalProjectsModel = new PersonalProjectSelectionTableModel();

    table = new PersonalProjectSelectionTable(personalProjectsModel);

    // filters = new PatternFilter[] {
    // new PatternFilter(".*", Pattern.CASE_INSENSITIVE, 0),
    // new PatternFilter(".*", Pattern.CASE_INSENSITIVE, 2) };
    //
    // table.setFilters(new FilterPipeline(filters));

    JScrollPane scrl = new JScrollPane(table);
    add(scrl, BorderLayout.CENTER);
    add(getToolbar(), BorderLayout.NORTH);

  }

  private JToolBar getToolbar() {
    JToolBar toolbar = new JToolBar(SwingConstants.HORIZONTAL);
    toolbar.setLayout(new GridBagLayout());

    GridBagConstraints c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = 0;
    c.fill = GridBagConstraints.NONE;
    c.anchor = GridBagConstraints.WEST;
    c.weightx = 0.0;
    c.weighty = 0.0;

    // THEME_RC.getString("")

    URL imageURL = null;
    imageURL = Thread.currentThread().getContextClassLoader().getResource(
        THEME_RC.getString("icon.projects.reload"));
    ImageIcon addProjectIcon = new ImageIcon(imageURL, "refresh");

    JButton reloadProjects = new JButton(addProjectIcon);
    reloadProjects.setActionCommand(CMD_RELOAD);
    reloadProjects.setToolTipText(RB
        .getString("personalprojects.refresh.tooltip"));
    reloadProjects.addActionListener(this);
    toolbar.add(reloadProjects, c);

    c.gridx++;

    c.weightx = 0.5;
    toolbar.add(Box.createHorizontalGlue(), c);
    c.gridx++;
    c.weightx = 0.0;

    searchField = new JTextField(10);
    searchField.getDocument().addDocumentListener(this);
    searchField.setToolTipText(RB
        .getString("projectviewer.searchfield.tooltip"));

    c.insets = new Insets(0, 5, 0, 5);
    toolbar.add(new JLabel("Filter"), c);
    c.gridx++;
    c.insets = new Insets(0, 0, 0, 0);
    toolbar.add(searchField, c);

    toolbar.setRollover(true);
    toolbar.setFloatable(false);
    return toolbar;
  }

  public void actionPerformed(ActionEvent evt) {
    personalProjectsModel.reloadTableData();
  }

  @Override
  public void changedUpdate(DocumentEvent e) {
    searchFieldUpdated();
  }

  @Override
  public void insertUpdate(DocumentEvent e) {
    searchFieldUpdated();
  }

  @Override
  public void removeUpdate(DocumentEvent e) {
    searchFieldUpdated();
  }

}
