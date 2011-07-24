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
 * Created on 26.11.2005 Author: osebelin
 */
package verdandi.ui.common;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.decorator.HighlighterFactory;

/**
 * A Table that stores the width of its columns in a preference object
 */
public abstract class WidthStoringTable extends JXTable {

  private static final long serialVersionUID = 1L;

  protected static final Log LOG = LogFactory.getLog(WidthStoringTable.class);

  /**
   * <code>col.width.</code>
   */
  protected static final String PREFIX_COL_WIDTH = "col.width.";

  protected String widthPref;

  protected WidthStoringTableModel myModel;

  /**
   * @param model
   */
  public WidthStoringTable(WidthStoringTableModel model) {
    super(model);
    myModel = model;
    widthPref = PREFIX_COL_WIDTH
        + getClass().getName().substring(
            getClass().getName().lastIndexOf(".") + 1);
    initWidths();

    // setHighlighters(new Highlighter[]{ AlternateRowHighlighter.linePrinter
    // });
    setHighlighters(new Highlighter[] { HighlighterFactory
        .createSimpleStriping() });

  }

  /**
   * Stores thw width of the columns to a preference. The preference is stored
   * beneath the node for the Package of the implementing class. Preferences are
   * named {@link #PREFIX_COL_WIDTH} plus the column index.
   * 
   * @see #PREFIX_COL_WIDTH
   */
  public void storeWidths() {
    Preferences prefs = Preferences.userNodeForPackage(getClass());
    LOG.debug("Storing widths to " + prefs.absolutePath() + "; "
        + getModel().getClass().getSimpleName());
    for (int i = 0; i < getModel().getColumnCount(); i++) {
      int width = getColumnModel().getColumn(i).getWidth();
      LOG.debug("Store  " + widthPref + i + "=" + width);
      prefs.putInt(widthPref + i, width);
    }
    try {
      prefs.flush();
    } catch (BackingStoreException e) {
      e.printStackTrace();
    }
  }

  /**
   * Initializes the column widths.
   */
  protected void initWidths() {
    LOG.debug("Initialising columns of table: " + getClass().getName());
    Preferences prefs = Preferences.userNodeForPackage(getClass());
    for (int i = 0; i < getModel().getColumnCount(); i++) {
      int width = prefs.getInt(widthPref + i, getColumnModel().getColumn(i)
          .getPreferredWidth());
      LOG.debug("Init width of col " + i + " to " + width);
      getColumnModel().getColumn(i).setPreferredWidth(width);
    }
  }
}
