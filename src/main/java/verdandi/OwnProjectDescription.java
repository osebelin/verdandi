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

import verdandi.model.CostUnit;

public class OwnProjectDescription {

  private String id;

  private CostUnit project;

  private String description;

  private boolean selected = false;

  public OwnProjectDescription() {
    super();
  }

  /**
   * @return the project
   */
  public CostUnit getProject() {
    return project;
  }

  /**
   * @param project
   *          the project to set
   */
  public void setProject(CostUnit project) {
    this.project = project;
    this.id = project.getId();
  }

  /**
   * @return the description
   */
  public String getDescription() {
    return description;
  }

  /**
   * @param description
   *          the description to set
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * @return the selected
   */
  public boolean isSelected() {
    return selected;
  }

  /**
   * @param selected
   *          the selected to set
   */
  public void setSelected(boolean selected) {
    this.selected = selected;
  }

  /**
   * @return the id
   */
  private String getId() {
    return id;
  }

  /**
   * @param id
   *          the id to set
   */
  private void setId(String id) {
    this.id = id;
  }

}
