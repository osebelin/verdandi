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
package verdandi;

import java.text.DecimalFormat;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;

/**
 * Standardized formatter for Durations.
 * 
 * Supports two modes: hh:mm and hh,hh/4.
 * 
 * Example 01:45 vs. 01,75
 * 
 * FIXME: Wasndasfürnscheiss? FOrmat muss als Parameter möglich sein!
 */
public class DurationFormatter implements PreferenceChangeListener {

  public static final String PREF_DISPLAY_MODE = "display_mode";

  public static final int DISPLAY_DURATION_HHMM = 1;

  public static final int DISPLAY_DURATION_HH_QUARTER = 2;

  private int displayMode = DISPLAY_DURATION_HHMM;

  private static DurationFormatter formatter = null;

  private static final DecimalFormat DCFMT = new DecimalFormat("00");

  private static Preferences prefs = Preferences
      .userNodeForPackage(DurationFormatter.class);

  protected DurationFormatter() {
    displayMode = prefs.getInt(PREF_DISPLAY_MODE, 1);
    prefs.addPreferenceChangeListener(this);
  }

  /**
   * @return Returns the displayMode.
   */
  public int getDisplayMode() {
    return displayMode;
  }

  /**
   * Sets the
   * 
   * @param displayMode
   *          The displayMode to set.
   */
  public static void setDisplayMode(int displayMode) {
    if (displayMode != DISPLAY_DURATION_HH_QUARTER
        && displayMode != DISPLAY_DURATION_HHMM) {
      throw new IllegalArgumentException("Unsupported display mode");
    }
    prefs.putInt(PREF_DISPLAY_MODE, displayMode);
  }

  public synchronized String format(int minutes) {
    StringBuffer res = new StringBuffer();
    res.append(DCFMT.format(minutes / 60));

    if (displayMode == DISPLAY_DURATION_HHMM) {
      res.append(":");
      res.append(DCFMT.format(minutes % 60));
    } else {
      res.append(",");
      int quarters = ((minutes % 60) / 15) * 25;
      res.append(DCFMT.format(quarters));
    }
    return res.toString();
  }

  public static DurationFormatter getFormatter() {
    if (formatter == null) {
      formatter = new DurationFormatter();
    }
    return formatter;
  }

  public void preferenceChange(PreferenceChangeEvent evt) {
    displayMode = prefs.getInt(PREF_DISPLAY_MODE, displayMode);
  }

}
