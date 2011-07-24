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
package verdandi.plugin;

import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import verdandi.VerdandiConfiguration;
import verdandi.model.VerdandiModel;

public class PluginLoader {

  private static final Log LOG = LogFactory.getLog(PluginLoader.class);

  private VerdandiConfiguration conf;

  private List<VerdandiPlugin> plugins = new ArrayList<VerdandiPlugin>();

  public PluginLoader() {
    super();
    this.conf = VerdandiModel.getConf();
    loadPlugins();
  }

  private boolean classAlreadyLoaded(Class<? extends VerdandiPlugin> pluginclass) {
    LOG.debug("Checking, if " + pluginclass.getName() + " is loaded");

    for (VerdandiPlugin loaded : plugins) {
      if (loaded.getClass().getName().equals(pluginclass.getName())) {
        LOG.info("Class already loaded. Will not load again: "
            + pluginclass.getName());
        return true;
      }
    }
    return false;

  }

  private void load(ClassLoader classLoader) {
    ServiceLoader<VerdandiPlugin> serviceLoader = ServiceLoader.load(
        VerdandiPlugin.class, classLoader);
    for (VerdandiPlugin verdandiPlugin : serviceLoader) {
      if (!classAlreadyLoaded(verdandiPlugin.getClass())) {
        plugins.add(verdandiPlugin);
      }
      LOG
          .debug("Loaded plugin "
              + verdandiPlugin.getClass().getCanonicalName());
    }

  }

  public void loadPlugins() {

    LOG.debug("Loading system plugins");
    load(Thread.currentThread().getContextClassLoader());

    LOG.debug("Now loading plugins from plugin dir "
        + conf.getPluginDir().getAbsolutePath());
    File pluginJars[] = conf.getPluginDir().listFiles(new FilenameFilter() {
      public boolean accept(File dir, String name) {
        return name.toLowerCase().endsWith(".jar");
      }
    });
    if (pluginJars != null) {

      for (File pluginJar : pluginJars) {
        try {
          ClassLoader pluginLoader = new URLClassLoader(new URL[] { pluginJar
              .toURI().toURL() }, Thread.currentThread()
              .getContextClassLoader());
          LOG.debug("Loading plugins from " + pluginJar.getName());
          load(pluginLoader);
        } catch (MalformedURLException e) {
          LOG
              .error("Cannot load casses from " + pluginJar.getAbsolutePath(),
                  e);
        }
      }
    } else {
      LOG.debug("No plugin jars found in "
          + conf.getPluginDir().getAbsolutePath());
    }

  }

  public List<VerdandiPlugin> getPlugins() {
    return plugins;
  }

}
