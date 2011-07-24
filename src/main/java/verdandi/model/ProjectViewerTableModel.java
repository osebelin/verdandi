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
package verdandi.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import verdandi.event.ErrorEvent;
import verdandi.persistence.PersistenceException;
import verdandi.persistence.PersistenceListener;
import verdandi.ui.common.WidthStoringTableModel;

/**
 */
@SuppressWarnings("serial")
public class ProjectViewerTableModel extends WidthStoringTableModel implements
    PersistenceListener {

  private static Log log = LogFactory.getLog(ProjectViewerTableModel.class);

  private List<CostUnit> projects;

  private boolean showActive = true;

  /**
   * @param parentModel
   */
  public ProjectViewerTableModel() {
    super(ResourceBundle.getBundle("TextResources"));
    VerdandiModel.getPersistence().addListener(this);
    reloadProjects();
    // try {
    // projects =
    // new ArrayList<Project>(parentModel.getPersistence().getProjects()
    // .values());
    // } catch (PersistenceException e) {
    // parentModel.fireEvent(new ErrorEvent(this, e));
    // projects = new ArrayList<Project>();
    // }
    // Collections.sort(projects, ProjectComparator.NAME_COMPARATOR);
  }

  public int getRowCount() {
    return projects.size();
  }

  public Object getValueAt(int row, int col) {
    CostUnit p = projects.get(row);
    switch (col) {
    case 0:
      return p.getId();
    case 1:
      return Boolean.valueOf(p.isActive());
    case 2:
      return p.getName();
    case 3:
      return p.getDescription();
    default:
      return null;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
   */
  @Override
  public Class<?> getColumnClass(int col) {
    switch (col) {
    // case 0:
    // return Number.class;
    case 1:
      return Boolean.class;
    default:
      return String.class;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
   */
  @Override
  public boolean isCellEditable(int row, int col) {
    return col == 1;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object, int,
   * int)
   */
  @Override
  public void setValueAt(Object val, int row, int col) {
    log.debug(row + "," + col + ": " + val);

    CostUnit p = projects.get(row);
    switch (col) {
    case 1:
      p.setActive(!p.isActive());
      try {
        VerdandiModel.getPersistence().save(p);
      } catch (PersistenceException e) {
        LOG.error("Cannot save project: ", e);
        VerdandiModel.fireEvent(new ErrorEvent(e));
      }
      return;
    default:
      return;
    }

  }

  public void reloadProjects() {
    LOG.debug("Reloading projects");
    try {
      if (showActive) {
        projects = new ArrayList<CostUnit>(VerdandiModel.getPersistence()
            .getProjects().values());
      } else {
        projects = VerdandiModel.getPersistence().getActiveProjectList();
      }
    } catch (PersistenceException e) {
      VerdandiModel.fireEvent(new ErrorEvent(e));
      projects = new ArrayList<CostUnit>();
    }
    Collections.sort(projects, ProjectComparator.ID_COMPARATOR);
    fireTableDataChanged();
  }

  @Override
  protected String[] getColumnNames() {
    return new String[] { "projectviewer.column.project.name",
        "projectviewer.column.active.name", "projectviewer.column.title.name",
        "projectviewer.column.description.name" };
  }

  public CostUnit getProject(int row) {
    return projects.get(row);
  }

  public void ownProjectSelectionChanged() {
  }

  public void projectChanged(CostUnit p) {
    reloadProjects();
  }

  public void workRecordChanged(WorkRecord timeSlot) {
  }

  public void persistenceInit() {
    reloadProjects();
  }

  public void userChanged(String uname) {
    // TODO Auto-generated method stub
  }

  /**
   * @return the showActive
   */
  public boolean isShowActive() {
    return showActive;
  }

  /**
   * @param showActive
   *          the showActive to set
   */
  public void setShowActive(boolean showActive) {
    this.showActive = showActive;
    reloadProjects();
  }

}
