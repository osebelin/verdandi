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
 * Created on 01.10.2005
 * Author: osebelin
 *
 */
package verdandi.model;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import verdandi.DurationFormatter;
import verdandi.SummaryItem;
import verdandi.SummaryItemComparator;
import verdandi.VerdandiException;
import verdandi.event.ErrorEvent;
import verdandi.persistence.PersistenceListener;
import verdandi.ui.common.WidthStoringTableModel;

@SuppressWarnings("serial")
public class SummaryViewModel extends WidthStoringTableModel implements
    CurrentWeekListener, PreferenceChangeListener, PersistenceListener {

  public static final int MODE_WEEK_OF_YEAR = 1;

  public static final int MODE_DATE_RANGE = 2;

  private static Log log = LogFactory.getLog(SummaryViewModel.class);

  private static ResourceBundle rb = ResourceBundle.getBundle("TextResources");

  private static final String RC_TABLE_PROJECTID_COLNAME = "summary.table.column.projectid.name";

  private static final String RC_TABLE_NAME_COLNAME = "summary.table.column.project.name";

  private static final String RC_TABLE_DURATION_COLNAME = "summary.table.column.duration.name";

  private List<SummaryItem> items;

  private int durationTotalMinutes = 0;

  /**
   * @param VerdandiModel
   */
  public SummaryViewModel(int mode) {
    super();
    VerdandiModel.getPersistence().addListener(this);

    if (mode == MODE_WEEK_OF_YEAR) {
      VerdandiModel.getCurrentWeekModel().addCurrentWeekListener(this);
      try {
        items = getDurationSummary(VerdandiModel.getCurrentWeekModel()
            .getCurrentWeek());
      } catch (VerdandiException e) {
        log.error("Cannot get List:", e);
      }
    } else {
      items = new ArrayList<SummaryItem>();
    }
    Collections.sort(items, SummaryItemComparator.ID_COMPARATOR);
    sumUpDurations();

    log.debug("rbtests: " + rb.getString("summary.table.total"));

    Preferences.userNodeForPackage(DurationFormatter.class)
        .addPreferenceChangeListener(this);

  }

  private void sumUpDurations() {
    durationTotalMinutes = 0;
    for (Iterator<SummaryItem> it = items.iterator(); it.hasNext();) {
      SummaryItem sumItem = it.next();
      durationTotalMinutes += sumItem.getDuration();
    }
  }

  public int getRowCount() {
    // return items.size() + 1;
    return items.size();
  }

  public Object getValueAt(int row, int col) {
    if (row < items.size()) {
      SummaryItem sumItem = items.get(row);
      if (col == 0) {
        return sumItem.getProjectid();
      } else if (col == 1) {
        return sumItem.getProjectName();
      } else if (col == 2) {
        return sumItem.getDurationAsString();
      } else {
        log.error("invalid column");
        return null;
      }
    } else {
      log.error("invalid row");
      return null;
    }
  }

  public void currentWeekChanged(Calendar currentWeek) {
    reloadTableData(currentWeek);
  }

  private void reloadTableData(Calendar currentWeek) {
    try {
      items = getDurationSummary(currentWeek);
      sumUpDurations();
      fireTableDataChanged();
    } catch (VerdandiException e) {
      log.error("Duration summary: ", e);
    }
  }

  public void preferenceChange(PreferenceChangeEvent arg0) {
    // format has changed -> repaint
    fireTableDataChanged();
  }

  public void ownProjectSelectionChanged() {
  }

  public void projectChanged(CostUnit p) {
    fireTableDataChanged();
  }

  public void workRecordChanged(WorkRecord timeSlot) {
    LOG.debug("Work record changed");
    try {
      items = getDurationSummary(VerdandiModel.getCurrentWeekModel()
          .getCurrentWeek());
      sumUpDurations();
      fireTableDataChanged();
    } catch (VerdandiException e) {
      log.error("Duration summarycalculation failed: ", e);
      VerdandiModel.fireEvent(new ErrorEvent(e));
    }
  }

  /**
   * @param currentWeek
   * @return The SUmmary for the current week described by the calendar.
   * @throws VerdandiException
   * 
   */
  protected List<SummaryItem> getDurationSummary(Calendar currentWeek)
      throws VerdandiException {
    currentWeek.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
    currentWeek.set(Calendar.HOUR_OF_DAY, 0);
    currentWeek.set(Calendar.MINUTE, 0);
    currentWeek.set(Calendar.SECOND, 0);
    currentWeek.set(Calendar.MILLISECOND, 0);
    Date dStart = new Date(currentWeek.getTime().getTime());
    currentWeek.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
    currentWeek.set(Calendar.HOUR_OF_DAY, 23);
    currentWeek.set(Calendar.MINUTE, 59);
    currentWeek.set(Calendar.SECOND, 59);
    currentWeek.set(Calendar.MILLISECOND, 999);
    Date dEnd = new Date(currentWeek.getTime().getTime());
    if (LOG.isDebugEnabled()) {
      DateFormat dfmt = DateFormat.getDateTimeInstance(DateFormat.MEDIUM,
          DateFormat.MEDIUM);
      LOG.debug("Fetching duration summaries from " + dfmt.format(dStart)
          + " to " + dEnd);
    }
    return VerdandiModel.getPersistence().getDurationSummaries(dStart, dEnd,
        false);
  }

  @Override
  protected String[] getColumnNames() {
    return new String[] { rb.getString(RC_TABLE_PROJECTID_COLNAME),
        rb.getString(RC_TABLE_NAME_COLNAME),
        rb.getString(RC_TABLE_DURATION_COLNAME) };

  }

  /**
   * @return the durationTotalMinutes
   */
  public int getDurationTotalMinutes() {
    return durationTotalMinutes;
  }

  public void persistenceInit() {
    log.debug("Persistence Changed: Repainting");
    reloadTableData(VerdandiModel.getCurrentWeekModel().getCurrentWeek());
  }

  public void userChanged(String uname) {
    // TODO Auto-generated method stub
  }

}
