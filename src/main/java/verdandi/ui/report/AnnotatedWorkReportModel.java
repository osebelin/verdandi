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
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import verdandi.event.ErrorEvent;
import verdandi.model.CostUnit;
import verdandi.model.VerdandiModel;
import verdandi.model.WorkRecord;
import verdandi.persistence.PersistenceException;
import verdandi.persistence.PersistenceListener;

public class AnnotatedWorkReportModel implements PersistenceListener {

  protected Date intervalStart, intervalEnd;

  protected List<CostUnit> selectedProjects = new ArrayList<CostUnit>();

  protected List<WorkRecord> records = new ArrayList<WorkRecord>();

  protected int durationSum = 0;

  protected List<AnnotatedWorkReportModelListener> listeners = new ArrayList<AnnotatedWorkReportModelListener>();

  protected AnnotatedWorkReportModel() {
    super();
    VerdandiModel.getPersistence().addListener(this);

    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.DAY_OF_MONTH, 1);
    cal.set(Calendar.HOUR_OF_DAY, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);
    intervalStart = cal.getTime();

    cal.add(Calendar.MONTH, 1);
    cal.add(Calendar.DAY_OF_MONTH, -1);
    cal.set(Calendar.HOUR_OF_DAY, 23);
    cal.set(Calendar.MINUTE, 59);
    cal.set(Calendar.SECOND, 59);
    intervalEnd = cal.getTime();
  }

  protected void buildRecordList() {

    List<WorkRecord> raw = null;
    try {
      raw = VerdandiModel.getPersistence().getWorkRecords(intervalStart,
          intervalEnd);
    } catch (PersistenceException e) {
      VerdandiModel.fireEvent(new ErrorEvent(e));
      return;
    }
    records = new ArrayList<WorkRecord>();
    durationSum = 0;

    for (WorkRecord r : raw) {
      if (selectedProjects.contains(r.getAssociatedProject())) {
        records.add(r);
        durationSum += r.getDuration();
      }
    }
    fireRecordsChanged();
  }

  private void fireRecordsChanged() {
    for (AnnotatedWorkReportModelListener listener : listeners) {
      listener.recordsChanged();
    }
  }

  /**
   * @return the intervalStart
   */
  public Date getIntervalStart() {
    return intervalStart;
  }

  /**
   * @param intervalStart
   *          the intervalStart to set
   */
  public void setIntervalStart(Date intervalStart) {
    this.intervalStart = intervalStart;
    buildRecordList();
  }

  /**
   * @return the intervalEnd
   */
  public Date getIntervalEnd() {
    return intervalEnd;
  }

  /**
   * @param intervalEnd
   *          the intervalEnd to set
   */
  public void setIntervalEnd(Date intervalEnd) {
    this.intervalEnd = intervalEnd;
    buildRecordList();
  }

  /**
   * @return the selectedProjects
   */
  public List<CostUnit> getSelectedProjects() {
    return selectedProjects;
  }

  /**
   * @param selectedProjects
   *          the selectedProjects to set
   */
  public void setSelectedProjects(List<CostUnit> selectedProjects) {
    this.selectedProjects = selectedProjects;
    buildRecordList();
  }

  public boolean addListener(AnnotatedWorkReportModelListener listener) {
    return listeners.add(listener);
  }

  public boolean removeListener(AnnotatedWorkReportModelListener listener) {
    return listeners.remove(listener);
  }

  /**
   * @return the records
   */
  public List<WorkRecord> getRecords() {
    return records;
  }

  @Override
  public void ownProjectSelectionChanged() {
    // TODO: dirty project list!
    buildRecordList();
  }

  @Override
  public void persistenceInit() {
    buildRecordList();
  }

  @Override
  public void projectChanged(CostUnit p) {
    buildRecordList();
  }

  @Override
  public void userChanged(String uname) {
    buildRecordList();
  }

  @Override
  public void workRecordChanged(WorkRecord workRecord) {
    buildRecordList();
  }

  /**
   * Returns the sum of all durations of this report.
   */
  public int getDurationSum() {
    return durationSum;
  }

}
