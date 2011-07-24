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
 * Created on 26.11.2005
 * Author: osebelin
 *
 */
package verdandi.ui.common;

import java.util.ResourceBundle;

import javax.swing.table.AbstractTableModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Width storing tbale model.
 * 
 * Also supports i18n
 * 
 * An Implementing class must provide
 */
public abstract class WidthStoringTableModel extends AbstractTableModel {

  private static final long serialVersionUID = 1L;

  protected static final Log LOG =
    LogFactory.getLog(WidthStoringTableModel.class);

  protected String columnNames[];

  /**
   * Column names are taken literally in this case fom {@link #getColumnNames()}
   * .
   */
  public WidthStoringTableModel() {
    columnNames = getColumnNames();
  }

  /**
   * 
   * I18n initialization. Implementing class must provide placeholders for
   * ResourceBundle lookup in this case.
   * 
   * @param rc
   *          A ResourceBundle containg a column name mapping
   * @see #COLNAME_PREFIX
   * @see #getColumnNames()
   */
  public WidthStoringTableModel(ResourceBundle rc) {
    String columnNamePlaceHolders[] = getColumnNames();
    columnNames = new String[columnNamePlaceHolders.length];
    for (int i = 0; i < columnNamePlaceHolders.length; i++) {
      columnNames[i] = rc.getString(columnNamePlaceHolders[i]);
      LOG.debug("Setting name of column " + i + " to " + columnNames[i]);
    }
  }

  /**
   * 
   * 
   * @return the name provided by the implementing class
   * 
   * @see javax.swing.table.TableModel#getColumnName(int)
   */
  @Override
  public String getColumnName(int col) {
    return columnNames[col];
  }

  public int getColumnCount() {
    return columnNames.length;
  }

  /**
   * Give the column names
   * 
   * @return
   */
  protected abstract String[] getColumnNames();

}
