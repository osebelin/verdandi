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
import java.net.URL;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.UIManager;

import org.jdesktop.swingx.JXDatePicker;

public class JXDatePickerFactory {

  private static final ResourceBundle RC = ResourceBundle
      .getBundle("TextResources");

  private static final ResourceBundle THEME_RC = ResourceBundle
      .getBundle("theme");

  private static JXDatePickerFactory factory = null;

  private ImageIcon dropDownIcon = null;

  private JXDatePickerFactory() {
    URL imageURL = null;
    String imgSrc = THEME_RC.getString("icon.datepicker.select");
    imageURL = Thread.currentThread().getContextClassLoader().getResource(
        imgSrc);
    dropDownIcon = new ImageIcon(imageURL);
    UIManager.put("JXDatePicker.arrowDown.image", dropDownIcon);
  }

  public JXDatePicker getDatePicker() {
    return getDatePicker(new Date());
  }

  public JXDatePicker getDatePicker(Date initialDate) {
    JXDatePicker res = new JXDatePicker();
    res.setDate(initialDate);
    res.setFormats(DateFormat.getDateInstance(DateFormat.MEDIUM));
    // res.setLinkDate(System.currentTimeMillis(), RC
    // .getString("datepicker.today.label"));
    res.setDate(new Date());
    res.getMonthView().setDayForeground(Calendar.SUNDAY, Color.RED);

    return res;
  }

  /**
   * @return the factory
   */
  public static JXDatePickerFactory getInstance() {
    if (factory == null) {
      factory = new JXDatePickerFactory();
    }
    return factory;
  }

}
