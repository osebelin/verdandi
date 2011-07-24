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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The model of the current week. needed in week view like in summary.
 * 
 */
public class CurrentWeekModel {

	private Calendar cal;
	
	private List<CurrentWeekListener> listeners;
	
	private static final Log LOG = LogFactory.getLog(CurrentWeekModel.class);
	
	public CurrentWeekModel() {
		super();
		cal = Calendar.getInstance();
		 listeners = new ArrayList<CurrentWeekListener>();
	}
	
	/**
	 * @param listener
	 * @return
	 */
	public boolean addCurrentWeekListener(CurrentWeekListener listener) {
		return listeners.add(listener);
	}

	/**
	 * @param listener
	 * @return
	 */
	public boolean removeCurrentWeekListener(CurrentWeekListener listener) {
		return listeners.remove(listener);
	}

	/**
	 * Notify listeners;
	 */
	public void fireCurrentWeekChanged() {
		Calendar newWeek = (Calendar) cal.clone();
    for (CurrentWeekListener listener: listeners) {
      listener.currentWeekChanged(newWeek);
    }
	}
	
	/**
	 * @param week
	 */
	@Deprecated
	public void setCurrentWeek(int week) {
		LOG.debug("Setting current week to "+ week);
		if (week >52) {
			week = week %52;
			LOG.debug("week > 52 rewritten to " + week);
		}
		cal.set(Calendar.WEEK_OF_YEAR, week);
		fireCurrentWeekChanged();
	}
	
	/**
	 * @return The week without year context
	 */
	public Calendar getCurrentWeek() {
		return (Calendar) cal.clone();
	}
	
	public void addToCurrentWeek(int diff) {
	  LOG.debug("Add to current week: " + diff);
	  cal.add(Calendar.WEEK_OF_YEAR, diff);
	  fireCurrentWeekChanged();
	}
	
	
	
	
}
