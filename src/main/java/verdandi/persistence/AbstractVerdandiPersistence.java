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
 * Created on 04.12.2005 Author: osebelin
 */
package verdandi.persistence;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import verdandi.VerdandiConfiguration;
import verdandi.event.ErrorEvent;
import verdandi.event.EventBoard;
import verdandi.model.CostUnit;
import verdandi.model.WorkRecord;

/**
 * Abstract base class for persistence.
 * <p>
 * Stores own project selection to a file;
 */
public abstract class AbstractVerdandiPersistence implements
    VerdandiPersistence {

  private static final Log LOG = LogFactory
      .getLog(AbstractVerdandiPersistence.class);

  private static ResourceBundle rb = ResourceBundle.getBundle("TextResources");

  protected VerdandiConfiguration conf;

  protected EventBoard board;

  protected List<PersistenceListener> listeners = new LinkedList<PersistenceListener>();

  /**
   * own project selection is generally stored in the file system.
   * 
   * 
   * TODO: Store this into the database.
   */
  protected Set<String> ownProjectSelection = null;

  /**
   * @param model
   * @deprecated. Use init()
   * 
   */
  @Deprecated
  public AbstractVerdandiPersistence(VerdandiConfiguration conf) {
    super();
    this.conf = conf;
    readOwnProjectSelection();
  }

  public AbstractVerdandiPersistence() {
    super();
  }

  public void init(VerdandiConfiguration conf, EventBoard board)
      throws PersistenceException {
    this.conf = conf;
    this.board = board;
    conf.setPersistenceConfigurationDescription(getConfigurationDescription());
    firePersistenceInit();
  }

  /**
   * {@inheritDoc}
   */
  public void addListener(PersistenceListener listener) {
    listeners.add(listener);
  }

  /**
   * {@inheritDoc}
   */
  public void removeListener(PersistenceListener listener) {
    listeners.remove(listener);
  }

  protected void readOwnProjectSelection() {
    Map<String, CostUnit> projects;
    try {
      projects = getProjects();
    } catch (PersistenceException e) {
      board.fireEvent(new ErrorEvent(e));
      projects = new HashMap<String, CostUnit>();
    }
    if (projects == null) {
      LOG.debug("No Projects yet. Cannot Read Own Project Selection.");
      return;
    }
    ownProjectSelection = new HashSet<String>();
    File projectSel = conf.getOwnProjectSelectionFile();
    LOG.debug("Loading own projects selection from "
        + projectSel.getAbsolutePath());

    if (projectSel.exists() && projectSel.canRead()) {
      FileInputStream fIn = null;
      BufferedReader reader = null;
      try {
        fIn = new FileInputStream(projectSel);
        reader = new BufferedReader(new InputStreamReader(fIn));
        for (String line = reader.readLine(); line != null; line = reader
            .readLine()) {
          line = line.trim();
          if (line.startsWith("#") || "".equals(line)) {
            continue;
          }
          try {
            if (projects.containsKey(line) && projects.get(line).isActive()) {
              ownProjectSelection.add(line);
            }
          } catch (NumberFormatException e) {
            LOG.error("Invalid project id: " + line);
          }
        }
      } catch (IOException e) {
        LOG.error("Cannot read own projects:", e);
      } finally {
        if (fIn != null) {
          try {
            fIn.close();
          } catch (Throwable t) {
          }
        }

        if (reader != null) {
          try {
            reader.close();
          } catch (Throwable t) {
          }
        }
      }
    } else {
      LOG.info("Own projects file does not exist or cannot be read: "
          + projectSel.getAbsolutePath());
    }

  }

  public synchronized Set<String> getOwnProjectSelection() {

    if (ownProjectSelection == null) {
      readOwnProjectSelection();
    }

    return ownProjectSelection;
  }

  public void storeOwnProjectSelection(Set<String> selection) {
    LOG.debug("Storing own project selection");
    File projectSel = conf.getOwnProjectSelectionFile();
    FileOutputStream fOut = null;

    try {
      fOut = new FileOutputStream(projectSel);
      PrintWriter writer = new PrintWriter(fOut);

      for (String projectId : selection) {
        writer.println(projectId);
      }
      writer.close();
      ownProjectSelection = selection;
      fireOwnProjectSelectionChanged();
    } catch (IOException e) {
      LOG.error("Cannot store project selection", e);
      // FIXME: THROW!
    } finally {
      if (fOut != null) {
        try {
          fOut.close();
        } catch (Throwable t) {
        }
      }
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @throws PersistenceException
   */
  public List<CostUnit> getOwnActiveProjects() throws PersistenceException {

    if (ownProjectSelection == null) {
      readOwnProjectSelection();
    }

    List<CostUnit> ownActiveProjects = new ArrayList<CostUnit>();
    for (CostUnit p : getActiveProjectList()) {
      if (ownProjectSelection.contains(p.getId())) {
        ownActiveProjects.add(p);
      }
    }
    return ownActiveProjects;
  }

  protected void fireWorkRecordChanged(WorkRecord t) {
    for (PersistenceListener listener : listeners) {
      listener.workRecordChanged(t);
    }
  }

  protected void fireProjectChanged(CostUnit p) {
    LOG.debug("Notifying" + listeners.size() + " listeners ");
    for (PersistenceListener listener : listeners) {
      LOG.debug("Notifying" + listener.getClass().getName());
      listener.projectChanged(p);
    }
  }

  protected void firePersistenceInit() {
    for (PersistenceListener listener : listeners) {
      listener.persistenceInit();
    }
  }

  protected void fireOwnProjectSelectionChanged() {
    for (PersistenceListener listener : listeners) {
      LOG.debug("Firing ownProjectSelectionChanged() to "
          + listener.getClass().getSimpleName());
      listener.ownProjectSelectionChanged();
    }
  }

}
