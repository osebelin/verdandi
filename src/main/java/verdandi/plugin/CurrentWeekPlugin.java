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
 * Created on 09.11.2006
 * Author: osebelin
 *
 */
package verdandi.plugin;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.JMenuItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import verdandi.model.VerdandiModel;
import verdandi.ui.common.Pair;

/**
 * Plugin for
 */
public abstract class CurrentWeekPlugin extends VerdandiPlugin {

  private static final Log LOG = LogFactory.getLog(CurrentWeekPlugin.class);

  public abstract JMenuItem getMenuEntry();

  /**
   * Gets the borders of the current week. If a months change is in between then
   * tow will be returned!
   * 
   * @return
   */
  protected List<Pair<Date, Date>> getCurrentWeekBorders() {
    List<Pair<Date, Date>> res = new ArrayList<Pair<Date, Date>>(2);

    Calendar currentWeek = VerdandiModel.getCurrentWeekModel().getCurrentWeek();
    currentWeek.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
    setToBeginningOfDay(currentWeek);
    int currentMonth = currentWeek.get(Calendar.MONTH);

    Date dStart = new Date(currentWeek.getTime().getTime());
    Date dEnd;

    while (currentWeek.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
      currentWeek.add(Calendar.DAY_OF_WEEK, 1);
      if (currentWeek.get(Calendar.MONTH) != currentMonth) {
        LOG.debug("Month change detected:" + currentWeek.getTime());
        currentMonth = currentWeek.get(Calendar.MONTH);
        // Einen tag zurück, weil es hier quasi zu spät ist
        currentWeek.add(Calendar.DAY_OF_WEEK, -1);

        setToEndOfDay(currentWeek);
        dEnd = new Date(currentWeek.getTime().getTime());
        res.add(new Pair<Date, Date>(dStart, dEnd));
        currentWeek.add(Calendar.DAY_OF_WEEK, 1);
        setToBeginningOfDay(currentWeek);
        dStart = new Date(currentWeek.getTime().getTime());
        setToEndOfDay(currentWeek);
      }

    }

    currentWeek.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
    setToEndOfDay(currentWeek);
    dEnd = new Date(currentWeek.getTime().getTime());
    res.add(new Pair<Date, Date>(dStart, dEnd));

    return res;
  }

  private void setToBeginningOfDay(Calendar cal) {
    cal.set(Calendar.HOUR_OF_DAY, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);
  }

  private void setToEndOfDay(Calendar cal) {
    cal.set(Calendar.HOUR_OF_DAY, 23);
    cal.set(Calendar.MINUTE, 59);
    cal.set(Calendar.SECOND, 59);
    cal.set(Calendar.MILLISECOND, 999);
  }

  private static class Zeitraum {
    private Date begin, end;

  }

}
