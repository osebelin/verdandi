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
package verdandi.ui;

import java.awt.Color;
import java.util.ResourceBundle;
import java.util.regex.PatternSyntaxException;

import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Common base class for personal and global project viewing panels
 * 
 */
public class ProjectViewingPanel extends JPanel {

  private static final long serialVersionUID = 1L;

  protected static final ResourceBundle RB = ResourceBundle
      .getBundle("TextResources");

  protected static final ResourceBundle THEME_RC = ResourceBundle
      .getBundle("theme");

  // protected PatternFilter[] filters;

  protected JTextField searchField;

  protected void searchFieldUpdated() {
    String sText = searchField.getText();
    try {
      searchField.setForeground(Color.BLACK);
      searchField.setToolTipText(RB
          .getString("projectviewer.searchfield.tooltip"));
      if (sText.startsWith("#")) {
        // filters[0].setPattern(".*" + sText.substring(1).trim() + ".*",
        // Pattern.CASE_INSENSITIVE);
        // filters[1].setPattern(".*", Pattern.CASE_INSENSITIVE);
      } else {
        // filters[1].setPattern(".*" + sText + ".*", Pattern.CASE_INSENSITIVE);
        // filters[0].setPattern(".*", Pattern.CASE_INSENSITIVE);
      }
    } catch (PatternSyntaxException uncompletedRegexp) {
      searchField.setForeground(Color.RED);
      String errTooltip = RB
          .getString("projectviewer.searchfield.tooltip.invalidregexp");
      errTooltip = errTooltip.replace("{1}", uncompletedRegexp
          .getLocalizedMessage());
      searchField.setToolTipText(errTooltip);
    }

  }

}
