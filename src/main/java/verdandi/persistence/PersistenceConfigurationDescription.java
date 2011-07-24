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
package verdandi.persistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Map.Entry;

/**
 * First primitive start for a persistence configuration description
 * 
 * @author osebelin
 * 
 */
public class PersistenceConfigurationDescription {

  private Map<String, ConfigItem> items = new HashMap<String, ConfigItem>();

  protected ResourceBundle rc;

  private List<String> orderedKeys = new ArrayList<String>(3);

  public PersistenceConfigurationDescription(ResourceBundle rc) {
    super();
    this.rc = rc;
  }

  public Set<Entry<String, ConfigItem>> entrySet() {
    return items.entrySet();
  }

  public PersistenceConfigurationDescription(ResourceBundle rc, String[] keys) {
    super();
    this.rc = rc;
    for (String _key : keys) {
      put(_key);
    }
  }

  public class ConfigItem {
    private String name;

    private String description;

    private String configKey;

    public ConfigItem(String name, String description, String configKey) {
      super();
      this.name = name;
      this.description = description;
      this.configKey = configKey;
    }

    private String getString(String key) {
      try {
        return rc.getString(key);
      } catch (MissingResourceException e) {
        return key;
      }
    }

    public String getDescription() {
      return getString(description);
    }

    public String getName() {
      return getString(name);
    }

    /**
     * Find the value under this key in the configuration.
     * 
     * @return
     */
    public String getConfigKey() {
      return getString(configKey);
    }
  }

  public ConfigItem get(Object key) {
    return items.get(key);
  }

  public ConfigItem put(String key, ConfigItem value) {
    orderedKeys.add(key);
    return items.put(key, value);
  }

  /**
   * Assumes the rc tokens are prepended by key!
   * 
   * @param key
   * @param value
   * @return
   */
  public ConfigItem put(String key) {
    ConfigItem item =
      new ConfigItem(key + ".name", key + ".desc", key + ".key");
    orderedKeys.add(key);
    return items.put(key, item);
  }

  public List<String> getOrderedKeys() {
    return orderedKeys;
  }

}
