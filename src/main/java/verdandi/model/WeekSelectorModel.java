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
 * Created on 30.09.2005
 * Author: osebelin
 *
 */
package verdandi.model;

import java.text.DateFormat;
import java.util.Calendar;

import javax.swing.AbstractSpinnerModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class WeekSelectorModel extends AbstractSpinnerModel {

  private Calendar cal;

  private static final String WEEK_OF_YEAR_PREFIX = "KW ";

  private static final Log LOG = LogFactory.getLog(WeekSelectorModel.class);

  private DateFormat dfmt = DateFormat.getDateInstance(DateFormat.SHORT);

  private CurrentWeekModel parent;

  public WeekSelectorModel(CurrentWeekModel parentModel) {
    super();
    parent = parentModel;
    cal = parentModel.getCurrentWeek();
    cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
  }

  private String getDateValue(int diff) {
    Calendar other = (Calendar) cal.clone();
    StringBuffer buf = new StringBuffer();
    buf.append(WEEK_OF_YEAR_PREFIX);
    if (diff != 0) {
      other.add(Calendar.WEEK_OF_YEAR, diff);
      LOG.debug("Current date: " + cal.getTime() + ", New Date: "
          + other.getTime() + ", diff: " + diff);

    }
    if (other.get(Calendar.WEEK_OF_YEAR) < 10) {
      buf.append("0");
    }
    buf.append(other.get(Calendar.WEEK_OF_YEAR));
    buf.append(": ");
    other.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
    buf.append(dfmt.format(other.getTime()));
    buf.append(" - ");
    other.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
    buf.append(dfmt.format(other.getTime()));
    return buf.toString();
  }

  private String getDateValue() {
    StringBuffer buf = new StringBuffer();
    Calendar cal = (Calendar) parent.getCurrentWeek().clone();
    buf.append(WEEK_OF_YEAR_PREFIX);
    if (cal.get(Calendar.WEEK_OF_YEAR) < 10) {
      buf.append("0");
    }
    buf.append(cal.get(Calendar.WEEK_OF_YEAR));
    buf.append(": ");
    cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
    buf.append(dfmt.format(cal.getTime()));
    buf.append(" - ");
    cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
    buf.append(dfmt.format(cal.getTime()));
    return buf.toString();
  }

  public Object getNextValue() {
    parent.addToCurrentWeek(1);
    return getDateValue();
  }

  public Object getPreviousValue() {
    parent.addToCurrentWeek(-1);
    return getDateValue();
  }

  public Object getValue() {
    return getDateValue();
  }

  public void setValue(Object val) {
    fireStateChanged();
  }

  private String getDateRep(int dayOfWeek) {
    Calendar res = (Calendar) cal.clone();
    res.set(Calendar.DAY_OF_WEEK, dayOfWeek);
    return dfmt.format(res.getTime());
  }

  public String getCurrentMondaySundayRange() {
    return getDateRep(Calendar.MONDAY) + " - " + getDateRep(Calendar.SUNDAY);
  }

}
