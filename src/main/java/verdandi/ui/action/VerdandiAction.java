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
package verdandi.ui.action;

import java.util.ResourceBundle;

import javax.swing.AbstractAction;

public abstract class VerdandiAction extends AbstractAction {

  private static final long serialVersionUID = 1L;

  protected static final ResourceBundle RC = ResourceBundle
      .getBundle("TextResources");

  protected static final ResourceBundle THEME_RC = ResourceBundle
      .getBundle("theme");

  /**
   * @param nameKey
   *          The Key for the action name in {@link #RC} or a litera;
   * @param model
   */
  protected VerdandiAction(String nameKey) {
    super(resolve(nameKey));
  }

  private static String resolve(String keyOrValue) {
    if (keyOrValue == null) {
      return keyOrValue;
    }
    if (RC.containsKey(keyOrValue)) {
      return RC.getString(keyOrValue);
    }
    return keyOrValue;
  }

}
