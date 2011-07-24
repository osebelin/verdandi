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
 * Created on 29.10.2006
 * Author: osebelin
 *
 */
package verdandi.model;

import java.util.ResourceBundle;

/**
 * Funny OO
 */
public class SummaryTotalItem {

  private static ResourceBundle rb = ResourceBundle.getBundle("TextResources");
  
  static final SummaryTotalItem EMPTY=new SummaryTotalItem("");
  
  static final SummaryTotalItem TOTAL=new SummaryTotalItem(rb.getString("summary.table.total"));
  
  private String data;

  /**
   * @param data
   */
  public SummaryTotalItem(String data) {
    super();
    this.data = data;
  }
  /** 
     * {@inheritDoc}
     */
  @Override
  public int hashCode() {
    return data.hashCode();
  }

  /** 
     * {@inheritDoc}
     */
  @Override
  public String toString() {
    return data;
  }
  
  
  
  
}
