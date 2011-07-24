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
package verdandi.ui;

import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import verdandi.model.CurrentWeekModel;
import verdandi.model.WeekSelectorModel;

@SuppressWarnings("serial")
public class WeekSelector extends JSpinner implements ChangeListener{

	private static final Log LOG = LogFactory.getLog(WeekSelector.class);
	
	
	
	/**
	 * 
	 */
	public WeekSelector(CurrentWeekModel model) {
		super(new WeekSelectorModel(model));
		addChangeListener(this);
	}

	public void stateChanged(ChangeEvent evt) {
		LOG.debug("State changed:" + evt);
	}
	
	
	
}
