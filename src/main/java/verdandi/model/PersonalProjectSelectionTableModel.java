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
 * Created on 03.12.2005 Author: osebelin
 */
package verdandi.model;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import verdandi.event.ErrorEvent;
import verdandi.persistence.PersistenceException;
import verdandi.persistence.PersistenceListener;
import verdandi.ui.common.WidthStoringTableModel;

@SuppressWarnings("serial")
public class PersonalProjectSelectionTableModel extends WidthStoringTableModel
    implements PersistenceListener {

  private static ResourceBundle rb = ResourceBundle.getBundle("TextResources");

  private List<CostUnit> items;

  private Set<String> selectedProjects;

  /**
   * @param parentModel
   */
  public PersonalProjectSelectionTableModel() {
    super(ResourceBundle.getBundle("TextResources"));
    VerdandiModel.getPersistence().addListener(this);
    try {
      items = VerdandiModel.getPersistence().getActiveProjectList();
    } catch (PersistenceException e) {
      VerdandiModel.fireEvent(new ErrorEvent(e));
      items = new ArrayList<CostUnit>();
    }
    selectedProjects = VerdandiModel.getPersistence().getOwnProjectSelection();
  }

  @Override
  protected String[] getColumnNames() {
    return new String[] { "projectviewer.column.project.name",
        "projectviewer.column.active.name", "projectviewer.column.title.name",
        "projectviewer.column.description.name" };
  }

  public int getRowCount() {
    return items.size();
  }

  public CostUnit getProject(int row) {
    return items.get(row);
  }

  public Object getValueAt(int row, int col) {
    CostUnit p = items.get(row);
    switch (col) {
    case 0:
      return p.getId();
    case 1:
      return Boolean.valueOf(selectedProjects.contains(p.getId()));
    case 2:
      return p.getName();
    case 3:
      return p.getDescription();
    default:
      return null;
    }
  }

  /**
   * The active yes/no Column is editable.
   * 
   * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
   */
  @Override
  public boolean isCellEditable(int row, int col) {
    return (col == 1);
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object, int,
   * int)
   */
  @Override
  public void setValueAt(Object val, int row, int col) {

    CostUnit p = items.get(row);
    LOG.debug("Setting Object at " + row + ", " + col + " to " + val
        + " Projectid=" + p.getId());
    if (col == 1) {
      Boolean b = (Boolean) val;
      if (b.booleanValue()) {
        LOG.debug("enabling");
        selectedProjects.add(p.getId());
      } else {
        selectedProjects.remove(p.getId());
        LOG.debug("disabling");
      }
    }
    VerdandiModel.getPersistence().storeOwnProjectSelection(selectedProjects);
  }

  /**
   * Column 1 is Boolean, others are String.
   * 
   * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
   */
  @Override
  public Class<?> getColumnClass(int col) {
    switch (col) {
    case 1:
      return Boolean.class;
    default:
      return String.class;
    }
  }

  public void reloadTableData() {
    LOG.debug("Reloading");
    long t0 = System.currentTimeMillis();
    selectedProjects = VerdandiModel.getPersistence().getOwnProjectSelection();
    LOG.debug("Number of Own projects: " + selectedProjects.size());
    try {
      items = VerdandiModel.getPersistence().getActiveProjectList();
    } catch (PersistenceException e) {
      VerdandiModel.fireEvent(new ErrorEvent(e));
      items = new ArrayList<CostUnit>();
    }
    LOG.debug("Reloaded userTable data. Number of items: " + items.size());
    long t1 = System.currentTimeMillis();
    System.out.println("before firing: " + (t1 - t0) + " ms.");
    fireTableDataChanged();
    long t2 = System.currentTimeMillis();
    System.out.println("reloadTableData in " + (t2 - t0) + " ms.");
  }

  public void ownProjectSelectionChanged() {
    reloadTableData();
  }

  public void persistenceInit() {
    reloadTableData();
  }

  public void projectChanged(CostUnit p) {
    reloadTableData();
  }

  public void workRecordChanged(WorkRecord timeSlot) {
  }

  public void userChanged(String uname) {
    // TODO Auto-generated method stub
  }

}
