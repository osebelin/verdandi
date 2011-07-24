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
package verdandi.ui.report;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.AbstractListModel;

import verdandi.event.ErrorEvent;
import verdandi.model.CostUnit;
import verdandi.model.ProjectComparator;
import verdandi.model.VerdandiModel;
import verdandi.model.WorkRecord;
import verdandi.persistence.PersistenceException;
import verdandi.persistence.PersistenceListener;

public class ProjectSelectorModel extends AbstractListModel implements
    PersistenceListener {
  private static final long serialVersionUID = 1L;

  protected AnnotatedWorkReportModel parentModel;

  protected CostUnit selectedProject = null;

  protected List<CostUnit> ownProjects;

  protected ProjectSelectorModel(AnnotatedWorkReportModel parentModel) {
    super();
    this.parentModel = parentModel;
    VerdandiModel.getPersistence().addListener(this);
    reloadProjects();
    if (!ownProjects.isEmpty()) {
      selectedProject = ownProjects.get(0);
    }

  }

  @Override
  public Object getElementAt(int index) {
    return ownProjects.get(index);
  }

  @Override
  public int getSize() {
    return ownProjects.size();
  }

  private void reloadProjects() {
    try {
      List<CostUnit> unsortedProjects = VerdandiModel.getPersistence()
          .getOwnActiveProjects();
      ;

      ownProjects = new ArrayList<CostUnit>(unsortedProjects.size());
      ownProjects.addAll(unsortedProjects);
      Collections.sort(ownProjects, ProjectComparator.NAME_COMPARATOR);
    } catch (PersistenceException e) {
      ownProjects = new ArrayList<CostUnit>(0);
      VerdandiModel.fireEvent(new ErrorEvent(e));
    }
    fireContentsChanged(this, 0, ownProjects.size());
  }

  @Override
  public void ownProjectSelectionChanged() {
    reloadProjects();
  }

  @Override
  public void persistenceInit() {
    reloadProjects();
  }

  @Override
  public void projectChanged(CostUnit p) {
    reloadProjects();
  }

  @Override
  public void userChanged(String uname) {
  }

  @Override
  public void workRecordChanged(WorkRecord workRecord) {
  }

}
