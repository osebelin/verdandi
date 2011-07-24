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
 * Created on 11.11.2005
 * Author: osebelin
 *
 */
package verdandi.persistence;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import verdandi.SummaryItem;
import verdandi.VerdandiConfiguration;
import verdandi.event.EventBoard;
import verdandi.model.CostUnit;
import verdandi.model.WorkRecord;

public interface VerdandiPersistence {

  public void addListener(PersistenceListener listener);

  public void removeListener(PersistenceListener listener);

  public void init(VerdandiConfiguration conf, EventBoard board)
      throws PersistenceException;

  /**
   * @return A List of TimeSlots
   * @throws PersistenceException
   */
  public List<WorkRecord> getWorkRecords(Date from, Date to)
      throws PersistenceException;

  /**
   * Saves a timeslot (INSERT or UPDATE)
   * 
   * @param workRecord
   * @throws PersistenceException
   */
  public void save(WorkRecord workRecord) throws PersistenceException;

  /**
   * @return Returns the projects.
   * @throws PersistenceException
   */
  public Map<String, CostUnit> getProjects() throws PersistenceException;

  public Set<String> getOwnProjectSelection();

  public void storeOwnProjectSelection(Set<String> selection);

  /**
   * Intersection of active projects and own projects.
   * 
   * @return
   * @throws PersistenceException
   */
  public List<CostUnit> getOwnActiveProjects() throws PersistenceException;

  public List<CostUnit> getActiveProjectList() throws PersistenceException;

  /**
   * Saves a StoredProject.
   * 
   * 
   * @param p
   * @throws PersistenceException
   */
  public void save(CostUnit p) throws PersistenceException;

  public void saveAll(Collection<CostUnit> projects, SaveProgressMonitor monitor)
      throws PersistenceException;

  public void delete(WorkRecord timeSlot) throws PersistenceException;

  /**
   * @param start
   * @param end
   * @param groupByAnnotation
   *          <code>true</code> to add a group by clause for the WorkRecords
   *          annotation
   * @return A List of
   * @throws PersistenceException
   */
  // TODO: add a boolean parameter: GroupByDescription!
  public List<SummaryItem> getDurationSummaries(Date start, Date end,
      boolean groupByAnnotation) throws PersistenceException;

  public PersistenceConfigurationDescription getConfigurationDescription();

}
