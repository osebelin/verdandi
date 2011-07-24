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
 * Created on 28.10.2006
 * Author: osebelin
 *
 */
package verdandi.persistence;

import verdandi.model.CostUnit;
import verdandi.model.VerdandiModel;
import verdandi.model.WorkRecord;


/**
 * @author osebelin
 * FIXME: Not yet deprecated cannot be replaced by EventBoard 
 * @see VerdandiModel
 * 
 */
public interface PersistenceListener {

  /**
   * A work record has been added, modified or deleted
   * 
   * @param workRecord
   */
  public void workRecordChanged(WorkRecord workRecord);

  /**
   * The selection list of own Projects has changed;
   */
  public void ownProjectSelectionChanged();

  /**
   * A Project as been added, modified or deleted
   */
  public void projectChanged(CostUnit p);

  /**
   * The persistence, or a delegate has initialized itself. Thus all data should
   * be reread.
   */
  public void persistenceInit();
  
  
  /**
   * @param uname
   */
  public void userChanged(String uname);

  

}
