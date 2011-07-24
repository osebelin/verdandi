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
package verdandi.ui;

import java.awt.Font;

import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

@SuppressWarnings("serial")
public class SumCellRenderer extends DefaultTableCellRenderer {

  private boolean alignRight=false;
  
  public SumCellRenderer(boolean doAlignRight) {
    alignRight=doAlignRight;
  }

  /* (non-Javadoc)
   * @see javax.swing.table.DefaultTableCellRenderer#setValue(java.lang.Object)
   */
  @Override
  protected void setValue(Object arg0) {
    setFont(new Font(getFont().getFontName(), Font.BOLD, getFont().getSize()));
    if (alignRight) {
      setHorizontalAlignment(SwingConstants.RIGHT);
    }
    setText(arg0.toString());  
  }
  
  
  
}
