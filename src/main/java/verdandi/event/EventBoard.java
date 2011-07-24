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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@SuppressWarnings("unchecked")
public class EventBoard {

  private static final Log LOG = LogFactory.getLog(EventBoard.class);

  protected Map<Class, Set<VerdandiEventListener>> listeners = new HashMap<Class, Set<VerdandiEventListener>>();

  public void addListener(VerdandiEventListener newListener, Class eventType) {
    eventType.getClass();

    Set<VerdandiEventListener> specificListeners = listeners.get(eventType);
    if (specificListeners == null) {
      specificListeners = new HashSet<VerdandiEventListener>();
      listeners.put(eventType, specificListeners);
    }
    specificListeners.add(newListener);
  }

  public <E extends VerdandiEvent> void fireEvent(E evt) {
    LOG.debug("Firing event " + evt.getClass().getSimpleName());
    for (Entry<Class, Set<VerdandiEventListener>> entry : listeners.entrySet()) {
      if (entry.getKey().isInstance(evt)) {
        for (VerdandiEventListener listener : entry.getValue()) {
          if (listener.eventOccured(evt)) {
            break;
          }
        }
      }
    }
  }

}
