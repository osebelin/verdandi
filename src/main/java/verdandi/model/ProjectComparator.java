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
 * Created on 28.09.2005
 * Author: osebelin
 *
 */
package verdandi.model;

import java.util.Comparator;


public class ProjectComparator implements Comparator<CostUnit> {

	public static final int MODE_ID=1;
	public static final int MODE_NAME=2;
	public static final int MODE_STATE=4;
	
	public static final ProjectComparator ID_COMPARATOR = new ProjectComparator(MODE_ID);
	public static final ProjectComparator NAME_COMPARATOR = new ProjectComparator(MODE_NAME);
	public static final ProjectComparator STATE_COMPARATOR = new ProjectComparator(MODE_STATE);
	
	private final int mode;
	
	private ProjectComparator(int mode) {
		this.mode=mode;
	}
	
	public int compare(CostUnit p1, CostUnit p2) {
    if (mode==MODE_ID) {
      return p1.getId().compareTo(p2.getId());
    } else if (mode==MODE_NAME) {
      return p1.getName().compareTo(p2.getName());
    } else if (mode==MODE_STATE) {
      if (p1.isActive() && p2.isActive()) {
//        System.out.println("T.c(T): 0" );
        return 0;
      } else if (p1.isActive()) {
//        System.out.println("T.c(F): -1");
        return -1;
      } else {
//        System.out.println("F.c(T): 1");
        return 1;
      }
      //Das sieht komisch aus.
//      return Boolean.valueOf(p1.isActive()).compareTo(Boolean.valueOf(p2.isActive()));
    }      
		throw new IllegalStateException("Unknown mode: " + mode);
	}



}
