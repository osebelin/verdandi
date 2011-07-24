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
package verdandi.helper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import verdandi.VerdandiConfiguration;
import verdandi.event.ApplicationShutdown;
import verdandi.event.EventBoard;
import verdandi.event.EventBroadcaster;
import verdandi.model.CostUnit;
import verdandi.model.persistence.VerdandiPersistenceWrapper;
import verdandi.persistence.PersistenceException;
import verdandi.persistence.VerdandiPersistence;

public class NavisionImporter {

  private File dataFile;

  private VerdandiPersistence persistence;

  /**
   * If non-null they will be appended to the project ids. Note that
   * workTypeCodes.length * project_coun ptorjects will be generated
   */
  private Set<String> workTypeCodes;

  private String workTypeCodeSeparator = "/";

  /**
   * TESTING/DEBUGGING ONLY!
   * 
   * @throws PersistenceException
   */
  public NavisionImporter() throws PersistenceException {
    dataFile = new File(
        "/home/osebelin/work/eclipse/verdandi/navision_in/navisionexport.csv");
    // persistence = new JPAPersistence();
    persistence = new VerdandiPersistenceWrapper();
    persistence.init(new VerdandiConfiguration(), new EventBoard());
  }

  /**
   * TESTING/DEBUGGING ONLY!
   * 
   * @throws PersistenceException
   */
  public NavisionImporter(File _dataFile) throws PersistenceException {
    dataFile = _dataFile;
    // persistence = new JPAPersistence();
    persistence = new VerdandiPersistenceWrapper();
    persistence.init(new VerdandiConfiguration(), new EventBoard());
  }

  public NavisionImporter(File _data, VerdandiPersistence _persistence)
      throws PersistenceException {
    dataFile = _data;
    persistence = _persistence;
  }

  public void inactivateAll() throws PersistenceException {
    int i = 0;
    for (CostUnit p : persistence.getActiveProjectList()) {
      System.out.print(".");
      if (i++ >= 80) {
        System.out.println();
        i = 0;
      }
      p.setActive(false);
      persistence.save(p);
    }
    System.out.println(" all inactive.\n\n");
  }

  public void readFile() throws IOException, PersistenceException {
    // BufferedReader br = new BufferedReader(new FileReader(dataFile));
    BufferedReader br = new BufferedReader(new InputStreamReader(
        new FileInputStream(dataFile), "ISO-8859-1"));
    int i = 0;
    for (String line = br.readLine(); line != null; line = br.readLine()) {

      String[] tokens = line.split("\\t");
      // for (int i = 0; i < tokens.length; i++) {
      // System.out.print("[" + i + "]=" + tokens[i] + ", ");
      // }
      // System.out.println();

      String pId = tokens[6].trim();
      if (pId.equals("")) {
        System.err.println("Empty project");
        continue;
      }
      if (!pId.matches("^[0-9].*")) {
        System.err.println("Skipped " + pId);
        continue;
      }

      String pName = tokens[8].trim();
      String srch = tokens[4].trim();
      String customer = tokens[5].trim();

      StringBuilder descBuf = new StringBuilder();
      if (!srch.equals("") && !srch.equalsIgnoreCase(pName)) {
        descBuf.append(srch).append(" \n");
      }

      if (!customer.equals("")) {
        descBuf.append("Kunde: ").append(customer);
      }

      String desc = descBuf.toString();

      if (workTypeCodes != null && !workTypeCodes.isEmpty()) {
        String projectIdBase = pId + workTypeCodeSeparator;

        for (String wt : workTypeCodes) {
          String actualProject = projectIdBase + wt;
          i++;
          CostUnit p = new CostUnit(actualProject, pName, desc);
          persistence.save(p);
          System.out.print(".");
          if (i % 80 == 0) {
            System.out.println(" " + i);
          }
        }
      } else {
        CostUnit p = new CostUnit(pId, pName, desc);
        persistence.save(p);
      }

    }
    System.out.println("\nDone: " + i);

  }

  public static void main(String[] args) {

    if (args.length != 1) {
      System.err.println("Usage: NavisionImporter <file>");
      System.exit(1);
    }

    try {
      NavisionImporter ni = new NavisionImporter(new File(args[0]));
      Set<String> wtc = new HashSet<String>(Arrays.asList(new String[] { "AUS",
          "DK", "DS", "IMP", "LUS", "MO", "PFLEGEN", "QS", "SUP" }));
      ni.setWorkTypeCodes(wtc);
      System.out.println("Deactivating all");
      ni.inactivateAll();
      System.out.println("Reading file");
      ni.readFile();
    } catch (Exception e) {
      e.printStackTrace();
    }
    EventBroadcaster.publish(new ApplicationShutdown());
  }

  /**
   * @return the workTypeCodes
   */
  public Set<String> getWorkTypeCodes() {
    return workTypeCodes;
  }

  /**
   * @param workTypeCodes
   *          the workTypeCodes to set
   */
  public void setWorkTypeCodes(Set<String> workTypeCodes) {
    this.workTypeCodes = workTypeCodes;
  }

  /**
   * @return the workTypeCodeSeparator
   */
  public String getWorkTypeCodeSeparator() {
    return workTypeCodeSeparator;
  }

  /**
   * @param workTypeCodeSeparator
   *          the workTypeCodeSeparator to set
   */
  public void setWorkTypeCodeSeparator(String workTypeCodeSeparator) {
    this.workTypeCodeSeparator = workTypeCodeSeparator;
  }

}
