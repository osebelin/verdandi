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

import verdandi.model.SummaryViewModel;
import verdandi.model.VerdandiListener;
import verdandi.model.VerdandiModel;

@SuppressWarnings("serial")
public class SummaryTable extends verdandi.ui.common.WidthStoringTable
    implements VerdandiListener {

  /**
   * @param parent
   */
  public SummaryTable(SummaryViewModel model) {
    super(model);
    VerdandiModel.addListener(this);
  }

  public void applicationQuit() {
    storeWidths();
  }

}
