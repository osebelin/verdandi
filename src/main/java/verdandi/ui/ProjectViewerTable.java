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

import java.awt.event.MouseEvent;

import verdandi.model.CostUnit;
import verdandi.model.ProjectViewerTableModel;
import verdandi.model.VerdandiListener;
import verdandi.model.VerdandiModel;

@SuppressWarnings("serial")
public class ProjectViewerTable extends verdandi.ui.common.WidthStoringTable
    implements VerdandiListener {

  private ProjectViewerTableModel model;

  public ProjectViewerTable(ProjectViewerTableModel model) {
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
    int rowBelowPointer = rowAtPoint(event.getPoint());
    if (rowBelowPointer < 0) {
      return "";
    }
    // int row = getFilters().convertRowIndexToModel(rowBelowPointer);
    int row = rowBelowPointer;
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
