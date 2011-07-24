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
 * Created on 11.03.2005 Author: osebelin
 */
package verdandi.ui;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import verdandi.model.CostUnit;
import verdandi.model.ProjectViewerTableModel;
import verdandi.model.VerdandiModel;
import verdandi.model.WorkRecord;
import verdandi.persistence.PersistenceListener;
import verdandi.ui.common.WindowUtilities;

public class ProjectViewerPanel extends ProjectViewingPanel implements
    ActionListener, TableModelListener, PersistenceListener, DocumentListener,
    MouseListener

{

  private static final Log LOG = LogFactory.getLog(ProjectViewerPanel.class);

  private static final long serialVersionUID = 3905246736095326771L;

  private static final String CMD_ADD_PROJECT = "addproject";

  private static final String CMD_EDIT_PROJECT = "editproject";

  private static final String CMD_IMPORT_PROJECT = "importproject";

  private static final String CMD_EXPORT_PROJECT = "exportproject";

  private static final String CMD_TOGGLE_SHOW_ACTIVE = "toggleshowactive";

  private final Cursor CURSOR_DEFAULT = new Cursor(Cursor.DEFAULT_CURSOR);

  private final Cursor CURSOR_WAIT = new Cursor(Cursor.WAIT_CURSOR);

  private JToggleButton toggleShowActive;

  private ImageIcon showActiveIcon, hideActiveIcon;

  private boolean showActive = true;

  private ProjectViewerTableModel tableModel;

  private ProjectViewerTable projectTable;

  /**
   * @param model
   * @throws HeadlessException
   */
  public ProjectViewerPanel() throws HeadlessException {
    super();
    VerdandiModel.getPersistence().addListener(this);
    initControls();
  }

  private void initControls() {
    setLayout(new BorderLayout());
    tableModel = new ProjectViewerTableModel();
    projectTable = new ProjectViewerTable(tableModel);
    // filters = new PatternFilter[] {
    // new PatternFilter(".*", Pattern.CASE_INSENSITIVE, 0),
    // new PatternFilter(".*", Pattern.CASE_INSENSITIVE, 2) };
    //
    // projectTable.setFilters(new FilterPipeline(filters));
    projectTable.addMouseListener(this);
    JScrollPane scrlTable = new JScrollPane(projectTable);
    add(getToolbar(), BorderLayout.NORTH);
    add(scrlTable, BorderLayout.CENTER);
  }

  private JToolBar getToolbar() {
    // JPanel toolbar = new JPanel();
    // toolbar.setLayout(new BoxLayout(toolbar, BoxLayout.LINE_AXIS));
    JToolBar toolbar = new JToolBar(SwingConstants.HORIZONTAL);

    toolbar.setLayout(new GridBagLayout());

    GridBagConstraints c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = 0;
    c.fill = GridBagConstraints.NONE;
    c.anchor = GridBagConstraints.WEST;
    c.weightx = 0.0;
    c.weighty = 0.0;

    URL imageURL = null;
    imageURL = Thread.currentThread().getContextClassLoader()
        .getResource(THEME_RC.getString("icon.project.add"));
    ImageIcon addProjectIcon = new ImageIcon(imageURL, "add");

    JButton addProject = new JButton(addProjectIcon);
    addProject.setActionCommand(CMD_ADD_PROJECT);
    addProject.setToolTipText(RB.getString("projectviewer.add.tooltip"));
    addProject.addActionListener(this);
    toolbar.add(addProject, c);
    c.gridx++;

    imageURL = getClass().getClassLoader().getResource(
        THEME_RC.getString("icon.project.edit"));
    ImageIcon editProjectIcon = new ImageIcon(imageURL, "edit");

    JButton editProject = new JButton(editProjectIcon);
    editProject.setActionCommand(CMD_EDIT_PROJECT);
    editProject.setToolTipText(RB.getString("projectviewer.edit.tooltip"));
    editProject.addActionListener(this);
    toolbar.add(editProject, c);
    c.gridx++;

    imageURL = getClass().getClassLoader().getResource(
        THEME_RC.getString("icon.projects.import"));
    ImageIcon importProjectIcon = new ImageIcon(imageURL, "import");
    JButton importProject = new JButton(importProjectIcon);
    importProject.setActionCommand(CMD_IMPORT_PROJECT);
    importProject.setToolTipText(RB.getString("projectviewer.import.tooltip"));
    importProject.addActionListener(this);
    toolbar.add(importProject, c);
    c.gridx++;

    // THEME_RC.getString("")
    imageURL = getClass().getClassLoader().getResource(
        THEME_RC.getString("icon.projects.export"));
    ImageIcon exportProjectIcon = new ImageIcon(imageURL, "export");
    JButton exportProject = new JButton(exportProjectIcon);
    exportProject.setActionCommand(CMD_EXPORT_PROJECT);
    exportProject.setToolTipText(RB.getString("projectviewer.export.tooltip"));
    exportProject.addActionListener(this);
    toolbar.add(exportProject, c);
    c.gridx++;

    toolbar.add(new JToolBar.Separator(), c);
    c.gridx++;

    imageURL = getClass().getClassLoader().getResource(
        THEME_RC.getString("icon.project.hide.active"));
    hideActiveIcon = new ImageIcon(imageURL, "hide active");
    imageURL = getClass().getClassLoader().getResource(
        THEME_RC.getString("icon.project.show.active"));
    showActiveIcon = new ImageIcon(imageURL, "show active");
    toggleShowActive = new JToggleButton(hideActiveIcon);
    toggleShowActive.setActionCommand(CMD_TOGGLE_SHOW_ACTIVE);
    toggleShowActive.setToolTipText(RB
        .getString("projectviewer.toggleshowactive.tooltip"));
    toggleShowActive.addActionListener(this);
    toolbar.add(toggleShowActive, c);
    c.gridx++;

    c.weightx = 0.5;
    toolbar.add(Box.createHorizontalGlue(), c);
    c.weightx = 0.0;
    c.gridx++;

    c.insets = new Insets(0, 5, 0, 5);
    toolbar.add(new JLabel("Filter"), c);
    c.gridx++;
    c.insets = new Insets(0, 0, 0, 0);
    searchField = new JTextField(10);
    searchField.getDocument().addDocumentListener(this);
    searchField.setToolTipText(RB
        .getString("projectviewer.searchfield.tooltip"));
    toolbar.add(searchField, c);

    toolbar.setFloatable(false);
    return toolbar;
  }

  private void editSelectedProject() {
    int selView = projectTable.getSelectedRow();
    if (selView < 0) {
      LOG.debug("NO row selected");
      return;
    }
    // int selModel = projectTable.getFilters().convertRowIndexToModel(selView);
    int selModel = selView;
    LOG.debug("SEL1: " + selView + ", SEL2: " + selModel);
    CostUnit p = tableModel.getProject(selModel);
    ProjectEditorDialog dlg = new ProjectEditorDialog(
        VerdandiModel.getDefaultParentFrame(), p);
    WindowUtilities.centerOnParent(dlg, VerdandiModel.getDefaultParentFrame());
    dlg.setVisible(true);
  }

  public void actionPerformed(ActionEvent evt) {
    if (evt.getActionCommand() == CMD_ADD_PROJECT) {
      CostUnit p = new CostUnit();
      p.setNewProject(true);
      ProjectEditorDialog dlg = new ProjectEditorDialog(
          VerdandiModel.getDefaultParentFrame(), p);
      WindowUtilities
          .centerOnParent(dlg, VerdandiModel.getDefaultParentFrame());
      dlg.setVisible(true);
    } else if (evt.getActionCommand() == CMD_EDIT_PROJECT) {
      editSelectedProject();
    } else if (evt.getActionCommand() == CMD_IMPORT_PROJECT) {
      importProjects();
    } else if (evt.getActionCommand() == CMD_EXPORT_PROJECT) {
      exportProjects();
    } else if (evt.getActionCommand() == CMD_TOGGLE_SHOW_ACTIVE) {
      showActive = !showActive;
      if (showActive) {
        toggleShowActive.setIcon(hideActiveIcon);
      } else {
        toggleShowActive.setIcon(showActiveIcon);
      }
      tableModel.setShowActive(showActive);
    } else {
      LOG.error("Unhandled Action: " + evt.getActionCommand());
    }

  }

  private void importProjects() {
    Preferences prefs = Preferences.userNodeForPackage(getClass());
    File importDir = new File(prefs.get("import.dir",
        System.getProperty("user.home")));

    JFileChooser chooser = new JFileChooser(importDir);
    chooser.setDialogTitle(RB
        .getString("projectviewer.import.filechooser.title"));
    chooser.setMultiSelectionEnabled(false);

    if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
      LOG.debug("User cancelled");
      return;
    }

    File importFile = chooser.getSelectedFile();

    prefs.put("import.dir", importFile.getParent());
    try {
      prefs.flush();
    } catch (BackingStoreException e) {
      LOG.error("Cannot write export file preference", e);
    }

    ObjectInputStream projectsIn = null;

    try {
      setCursor(CURSOR_WAIT);
      projectsIn = new ObjectInputStream(new FileInputStream(importFile));

      Object o = projectsIn.readObject();

      if (!(o instanceof Collection)) {
        LOG.error("The file does not contain a valid verdandi project list");
        return;
      }

      Collection<?> importcoll = (Collection<?>) o;
      @SuppressWarnings("unchecked")
      List<CostUnit> projects = (List<CostUnit>) o;
      new ProjectImporter(projects).start();
    } catch (FileNotFoundException e) {
      LOG.error("", e);
    } catch (IOException e) {
      LOG.error("No verdandi project list?", e);
    } catch (ClassNotFoundException e) {
      LOG.error("", e);
    } finally {
      setCursor(CURSOR_DEFAULT);
      if (projectsIn != null) {
        try {
          projectsIn.close();
        } catch (Throwable t) {
          LOG.error("Cannot close stream: ", t);
        }
      }
    }

  }

  private void exportProjects() {
    Preferences prefs = Preferences.userNodeForPackage(getClass());

    File exportDir = new File(prefs.get("export.dir",
        System.getProperty("user.home")));

    JFileChooser chooser = new JFileChooser(exportDir);
    chooser.setDialogTitle(RB
        .getString("projectviewer.export.filechooser.title"));
    chooser.setMultiSelectionEnabled(false);

    if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
      LOG.debug("User cancelled");
      return;
    }

    int[] selectedProjects = projectTable.getSelectedRows();

    if (selectedProjects.length == 0) {
      LOG.debug("NO row selected");
      return;
    }

    List<CostUnit> projectsToExport = new ArrayList<CostUnit>();

    for (int i = 0; i < selectedProjects.length; i++) {
      // int selModel = projectTable.getFilters().convertRowIndexToModel(
      // selectedProjects[i]);
      int selModel = selectedProjects[i];
      CostUnit p = tableModel.getProject(selModel);
      LOG.debug("Adding project to export list: " + p);
      projectsToExport.add(p);
    }

    File exportFile = chooser.getSelectedFile();
    LOG.debug("Exporting projects to " + exportFile.getAbsolutePath());

    ObjectOutputStream listOut = null;
    try {
      listOut = new ObjectOutputStream(new FileOutputStream(exportFile));
      listOut.writeObject(projectsToExport);
    } catch (FileNotFoundException e) {
      LOG.error("", e);
    } catch (IOException e) {
      LOG.error("", e);
    } finally {
      if (listOut != null) {
        try {
          listOut.close();
        } catch (Throwable t) {
          LOG.error("Cannot close stream: ", t);
        }
      }
    }

    prefs.put("export.dir", exportFile.getParent());
    try {
      prefs.flush();
    } catch (BackingStoreException e) {
      LOG.error("Cannot write export file preference", e);
    }

  }

  public void tableChanged(TableModelEvent evt) {
    LOG.debug("Table Changed!");
  }

  public void ownProjectSelectionChanged() {
  }

  public void projectChanged(CostUnit p) {
    projectTable.repaint();
  }

  public void workRecordChanged(WorkRecord timeSlot) {
  }

  public void persistenceInit() {
    projectTable.repaint();
  }

  public void userChanged(String uname) {
    projectTable.repaint();
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

  @Override
  public void mouseClicked(MouseEvent e) {
    if (e.getClickCount() > 1) {
      editSelectedProject();
    }
  }

  @Override
  public void mouseEntered(MouseEvent e) {
  }

  @Override
  public void mouseExited(MouseEvent e) {
  }

  @Override
  public void mousePressed(MouseEvent e) {
  }

  @Override
  public void mouseReleased(MouseEvent e) {
  }

}
