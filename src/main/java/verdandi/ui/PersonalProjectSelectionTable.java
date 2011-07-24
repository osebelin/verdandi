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
 * Created on 03.12.2005 Author: osebelin
 */
package verdandi.ui;

import java.awt.event.MouseEvent;

import verdandi.model.CostUnit;
import verdandi.model.PersonalProjectSelectionTableModel;
import verdandi.model.VerdandiListener;
import verdandi.model.VerdandiModel;

@SuppressWarnings("serial")
public class PersonalProjectSelectionTable extends
    verdandi.ui.common.WidthStoringTable implements VerdandiListener {

  private PersonalProjectSelectionTableModel model;

  public PersonalProjectSelectionTable(PersonalProjectSelectionTableModel model) {
    super(model);
    this.model = model;
    VerdandiModel.addListener(this);
  }

  public void applicationQuit() {
    storeWidths();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getToolTipText(MouseEvent event) {

    int row = rowAtPoint(event.getPoint());
    if (row < 0) {
      // No Row selected
      return "";
    }
    // row = getFilters().convertRowIndexToModel(row);
    if (row < 0) {
      // No Row selected
      return "";
    }

    CostUnit p = model.getProject(row);
    if (p != null) {
      String desc = p.getDescription();
      if (desc == null) {
        return "";
      }
      return "<html><body>" + desc.replaceAll("\n", "<br>") + "</body></html>";
    }

    return super.getToolTipText(event);
  }

}
