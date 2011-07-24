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
package verdandi.event;

import java.util.EventListener;

//FIXME: So ist das nicht so toll, weil man das nicht mehrmals implementieren kann
//Idee: Die interfaces als Hierarchie
public interface VerdandiEventListener extends EventListener {
  /**
   * @param t
   * @return <code>true</code> to abort processing of this event (consume only
   *         once);
   */
  public boolean eventOccured(VerdandiEvent event);
}
