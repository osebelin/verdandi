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

import java.util.Collection;

import verdandi.event.ErrorEvent;
import verdandi.model.CostUnit;
import verdandi.model.VerdandiModel;
import verdandi.persistence.PersistenceException;

public class ProjectImporter extends Thread {

  private Collection<CostUnit> projects;

  protected ProjectImporter(Collection<CostUnit> projects) {
    super();
    this.projects = projects;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void run() {
    try {
      ImportProgressView ipw = new ImportProgressView(VerdandiModel
          .getDefaultParentFrame());
      // ImportProgressView ipw = new ImportProgressView(null);
      ipw.setVisible(true);
      VerdandiModel.getPersistence().saveAll(projects, ipw);
    } catch (PersistenceException e) {
      VerdandiModel.fireEvent(new ErrorEvent(e));
    }

  }

}
