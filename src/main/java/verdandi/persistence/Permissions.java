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
 * Created on 19.11.2005
 * Author: osebelin
 *
 */
package verdandi.persistence;

import java.io.Serializable;

/**
 * @author osebelin
 * @deprecated Use Roles
 */
@Deprecated
public class Permissions implements Serializable {

  private static final long serialVersionUID = 1L;

  protected boolean mayEditProducts = false;
  
  protected boolean administrator=false;

  /**
   * @param user
   */
  public Permissions() {
    super();
  }

  /**
   * @return Returns the mayEditProducts.
   */
  public boolean isMayEditProducts() {
    return mayEditProducts;
  }

  /**
   * @param mayEditProducts The mayEditProducts to set.
   */
  public void setMayEditProducts(boolean mayEditProducts) {
    this.mayEditProducts = mayEditProducts;
  }

  /**
   * FIXME: NOt evaluated ATM!
   * 
   * @return Returns the administrator.
   */
  public boolean isAdministrator() {
    return administrator;
  }

  /**
   * @param administrator The administrator to set.
   */
  public void setAdministrator(boolean administrator) {
    this.administrator = administrator;
  }
  
  
}
