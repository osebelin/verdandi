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

import java.awt.Color;
import java.io.File;
import java.util.Observable;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import verdandi.persistence.PersistenceConfigurationDescription;

/**
 * Configuration
 */
public class VerdandiConfiguration extends Observable {

  private static final Log LOG = LogFactory.getLog(VerdandiConfiguration.class);

  private static final String PROPERTY_DB_HOST = "verdandi.db.host";

  private static final String PROPERTY_DB_NAME = "verdandi.db.name";

  public static final String KEY_DB_HOST = "db.host";

  public static final String KEY_DB_NAME = "db.name";

  public static final String KEY_DERBY_BASEDIR = "verdandi.derbypersistence.basedir";

  public static final String KEY_DERBY_DBNAME = "verdandi.derbypersistence.dbname";

  public static final String KEY_JPA_DERBY_DBNAME = "verdandi.jpa.derby.dbname";

  public static final String KEY_STORE_PASSWD = "store_passwd";

  public static final String KEY_SHOW_TIMER_ON_STARTUP = "show_timer_on_startup";

  public static final String KEY_UNAME = "uname";

  public static final String KEY_PASSWD = "passwd";

  public static final String KEY_HOUR_DISPLAY_MODE = "hour_display_mode";

  public static final String HOUR_DISPLAY_MODE_HHMM = "hour_display_mode_hhmm";

  public static final String HOUR_DISPLAY_MODE_HH_QUARTERS = "hour_display_mode_hh,hh/4";

  public static final String KEY_WORKDAY_COLOR1 = "workday_color_1";

  public static final String KEY_WORKDAY_COLOR2 = "workday_color_2";

  public static final String KEY_WORKDAY_START = "workday_start";

  public static final String KEY_WORKDAY_END = "workday_end";

  private static final String KEY_WORKDAY_SLOT_HEIGHT = "workday_slot_height";

  public static final String PERSISTENCE_TYPE_PG = "persistence_type_pg";

  public static final String VERDANDI_DIRNAME = ".verdandi";

  public static final String OWN_PROJECTS_FILE = "myprojects";

  private File basedir, pluginDir;

  private File ownProjectSelectionFile;

  private Preferences prefs = Preferences
      .userNodeForPackage(VerdandiConfiguration.class);

  private String passwd = null;

  public static int timeResolution = 15;

  private PersistenceConfigurationDescription persistenceConfigurationDescription = null;

  private boolean configOK = true;

  public VerdandiConfiguration() {
    super();
    File userHome = new File(System.getProperty("user.home"));
    basedir = new File(userHome, System.getProperty("verdandi.confdir",
        VERDANDI_DIRNAME));
    pluginDir = new File(basedir, "plugins");
    if (!basedir.exists()) {
      LOG.info("Creating base directory " + basedir.getAbsolutePath());
      if (!basedir.mkdir()) {
        LOG.fatal("Cannot create basedir " + basedir.getAbsolutePath());
        throw new RuntimeException("Cannot create basedir "
            + basedir.getAbsolutePath());
      }
    }
    if (!pluginDir.exists()) {
      LOG.info("Creating plugin directory " + pluginDir.getAbsolutePath());
      if (!pluginDir.mkdir()) {
        LOG.error("Cannto create plugin directory "
            + pluginDir.getAbsolutePath());
      }
    }
    ownProjectSelectionFile = new File(basedir, OWN_PROJECTS_FILE);

    if (isStorePasswd()) {
      passwd = prefs.get(KEY_PASSWD, null);
    }

    if (getString(KEY_DB_HOST).equals("") || getString(KEY_DB_NAME).equals("")) {
      LOG.debug("Database not configured. Trying system propertes.");
      if (System.getProperties().containsKey(PROPERTY_DB_HOST)
          && System.getProperties().containsKey(PROPERTY_DB_NAME)) {
        prefs.put(KEY_DB_HOST, System.getProperty(PROPERTY_DB_HOST));
        prefs.put(KEY_DB_NAME, System.getProperty(PROPERTY_DB_NAME));
        LOG.info("DB Host set by system property: " + getDbUrl());
      }
    } else {
      LOG.debug("Loaded Database settings from user configuration.");
    }

    if (getString(KEY_DERBY_BASEDIR).equals("")) {
      LOG.debug("Setting " + KEY_DERBY_BASEDIR + " to default value");
      prefs.put(KEY_DERBY_BASEDIR, getBasedir().getAbsolutePath());
    }
    // if (getString(KEY_DERBY_DBNAME).equals("")) {
    // LOG.debug("Setting " + KEY_DERBY_DBNAME + " to default value");
    // prefs.put(KEY_DERBY_DBNAME, DerbyPersistence.DEFAULT_DATABASE_NAME);
    // }

  }

  /**
   * 
   * If a system property <code>verdandi.db.url</code> exists, the url is read
   * from there. Else it is read from a resource file
   * <code>verdandi.properties</code> in the classpath.
   * 
   * @return the url of the database
   */
  public String getDbUrl() {
    return "jdbc:postgresql://" + getString(KEY_DB_HOST) + "/"
        + getString(KEY_DB_NAME);
  }

  public String getDbHost() {
    return getString(KEY_DB_HOST);
  }

  public String getDbName() {
    return getString(KEY_DB_NAME);
  }

  /**
   * Writes the preferneces to the backend
   * 
   * @throws BackingStoreException
   */
  public void storePrefs() throws BackingStoreException {
    prefs.sync();
  }

  /**
   * @return Returns the storePasswd.
   */
  public boolean isStorePasswd() {
    return prefs.getBoolean(KEY_STORE_PASSWD, false);
  }

  /**
   * @param storePasswd
   *          <code>true</code>, if the password should be stored
   */
  public void setStorePasswd(boolean storePasswd) {
    prefs.putBoolean(KEY_STORE_PASSWD, storePasswd);
    if (storePasswd && passwd != null) {
      prefs.put(KEY_PASSWD, passwd);
    } else {
      prefs.remove(KEY_PASSWD);
    }
    setChanged();
    notifyObservers(KEY_STORE_PASSWD);
  }

  public void setDBHost(String hostname) {
    prefs.put(KEY_DB_HOST, hostname);
  }

  public void setDBName(String databaseName) {
    prefs.put(KEY_DB_NAME, databaseName);
  }

  public void setShowTimerOnStartup(boolean show) {
    prefs.putBoolean(KEY_SHOW_TIMER_ON_STARTUP, show);
    setChanged();
    notifyObservers(KEY_SHOW_TIMER_ON_STARTUP);
  }

  public boolean isShowTimerOnStartup() {
    return prefs.getBoolean(KEY_SHOW_TIMER_ON_STARTUP, false);
  }

  /**
   * @return THe DB user name
   */
  public String getUname() {
    return prefs.get(KEY_UNAME, null);
  }

  public String getPasswd() {
    return passwd;
  }

  /**
   * Gives the mode of hour display
   * 
   * Possible are: hh:mm oder hh,(hh/4)
   * 
   * @return
   */
  public String getHourDisplayMode() {
    return prefs.get(KEY_HOUR_DISPLAY_MODE, HOUR_DISPLAY_MODE_HHMM);
  }

  public void setUname(String uname) {
    setConfigProperty(KEY_UNAME, uname);
    setChanged();
    notifyObservers(KEY_UNAME);
  }

  /**
   * @param key
   * @return the value, or an empty String, if not defined
   */
  public String getString(String key) {
    return getString(key, "");
  }

  /**
   * @param key
   * @param defaultValue
   * @return the value, or defaultValue, if not defined
   */
  public String getString(String key, String defaultValue) {
    return prefs.get(key, defaultValue);
  }

  /**
   * Sets the password, and stores it, if {@link #isStorePasswd()} equals
   * <code>true</code>.
   * 
   * @param passwd
   *          The password to set.
   */
  public void setPasswd(String password) {
    this.passwd = password;
    if (isStorePasswd()) {
      setConfigProperty(KEY_PASSWD, password);
      setChanged();
      notifyObservers(KEY_PASSWD);
    }
  }

  /**
   * @return Returns the ownProjectSelectionFile.
   */
  public File getOwnProjectSelectionFile() {
    return ownProjectSelectionFile;
  }

  public File getBasedir() {
    return basedir;
  }

  public File getPluginDir() {
    return pluginDir;
  }

  public boolean isConfigOK() {
    return configOK;
  }

  /**
   * Retrieves an individual config property.
   * 
   * Tries preferences first, then system properties.
   * 
   * 
   * @param key
   * @return value associated with key or <code>null</code>
   */
  public String getConfigProperty(String key) {
    String res = prefs.get(key, null);
    if (res == null) {
      LOG.debug("Cannot find key " + key + " in prefs. Trying system property.");
      res = System.getProperty(key);
    }
    return res;
  }

  /**
   * Retrieves an individual config property.
   * 
   * Tries preferences first, then system properties.
   * 
   * @param key
   * @param defaultValue
   * @return value associated with key or <code>defaultValue</code>
   */
  public String getConfigProperty(String key, String defaultValue) {
    String res = getConfigProperty(key);
    if (res == null) {
      return defaultValue;
    }
    return res;
  }

  /**
   * Allows to set an individual config property.
   * 
   * @param key
   * @param val
   *          if <code>null</code>, the config entry will be removed
   */
  public void setConfigProperty(String key, String val) {
    if (val == null) {
      LOG.debug("Removing " + key);
      prefs.remove(key);
    } else {
      prefs.put(key, val);
    }

  }

  public PersistenceConfigurationDescription getPersistenceConfigurationDescription() {
    return persistenceConfigurationDescription;
  }

  public void setPersistenceConfigurationDescription(
      PersistenceConfigurationDescription persistenceConfigurationDescription) {
    this.persistenceConfigurationDescription = persistenceConfigurationDescription;
  }

  public Color getWorkdayEditorColor() {
    return new Color(-8224056);
    // return Color.BLUE.brighter();
    // FIXME: Make editable
    // return new Color(prefs.getInt(KEY_WORKDAY_COLOR1,
    // WorkDayEditor.DEFAULT_COLOR1.getRGB()));
  }

  public Color getWorkdayEditorAlternativeColor() {
    return new Color(-7237251);
    // return new Color(prefs.getInt(KEY_WORKDAY_COLOR2,
    // WorkDayEditor.DEFAULT_COLOR2.getRGB()));
  }

  public void setWorkdayEditorColor(Color c) {
    prefs.putInt(KEY_WORKDAY_COLOR1, c.getRGB());
    setChanged();
    notifyObservers();
  }

  public void setWorkdayEditorAlternativeColor(Color c) {
    prefs.putInt(KEY_WORKDAY_COLOR2, c.getRGB());
    setChanged();
    notifyObservers();
  }

  public void setWorkDayParameter(int firstHour, int lastHour, int slotHeight) {
    prefs.putInt(KEY_WORKDAY_START, firstHour);
    prefs.putInt(KEY_WORKDAY_END, lastHour);
    prefs.putInt(KEY_WORKDAY_SLOT_HEIGHT, slotHeight);
    setChanged();
    notifyObservers();
  }

  public int getWorkDayFirstHour() {
    return prefs.getInt(KEY_WORKDAY_START, 8);
  }

  public int getWorkDayLastHour() {
    return prefs.getInt(KEY_WORKDAY_END, 19);
  }

  public int getWorkDaySlotHeight() {
    return prefs.getInt(KEY_WORKDAY_SLOT_HEIGHT, 10);
  }
}
