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
 * Created on 03.10.2005
 * Author: osebelin
 *
 */
package verdandi;

import java.util.Comparator;

public class SummaryItemComparator implements Comparator<SummaryItem> {

  
  public static final int MODE_ID=1;
  public static final int MODE_NAME=2;
  public static final int MODE_DURATION=4;
  
  public static final SummaryItemComparator ID_COMPARATOR = new SummaryItemComparator(MODE_ID);
  public static final SummaryItemComparator NAME_COMPARATOR = new SummaryItemComparator(MODE_NAME);
  public static final SummaryItemComparator DURATION_COMPARATOR = new SummaryItemComparator(MODE_DURATION);

  private int mode;

  
  /**
   * @param mode
   */
  private SummaryItemComparator(int mode) {
    super();
    this.mode = mode;
  }

  public int compare(SummaryItem summary1, SummaryItem summary2) {
    if (mode==MODE_ID) {
      return summary1.getProjectid().compareTo(summary2.getProjectid());
    } else if (mode==MODE_NAME) {
      return summary1.getProjectName().compareTo(summary2.getProjectName());
    } else if (mode==MODE_DURATION) {
      return (summary1.getDuration() - summary2.getDuration());
    } 
        return 0;
  }

}
