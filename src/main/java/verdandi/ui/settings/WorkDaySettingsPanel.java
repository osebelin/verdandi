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
package verdandi.ui.settings;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import verdandi.model.VerdandiModel;

public class WorkDaySettingsPanel extends SettingsPanel implements
    ActionListener {

  private static final long serialVersionUID = 1L;

  private ResourceBundle rc = ResourceBundle.getBundle("TextResources");

  private JComboBox workDayStart, workDayEnd;

  private JButton btnChooseWorkDayColor, btnChooseWorkDayAlternativeColor;

  private JTextField txtSlotHeight;

  private DecimalFormat dcfmt = new DecimalFormat("00");

  public WorkDaySettingsPanel() {
    super();
    setLayout(new GridBagLayout());
    initControls();
  }

  private void initControls() {

    String items[] = { "00", "01", "02", "03", "04", "05", "06", "07", "08",
        "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
        "21", "22", "23", "00" };

    workDayStart = new JComboBox(items);
    workDayStart.addActionListener(this);
    workDayEnd = new JComboBox(items);
    workDayEnd.addActionListener(this);

    GridBagConstraints c = new GridBagConstraints();
    c.insets = new Insets(5, 5, 5, 5);
    c.gridx = c.gridy = 0;
    c.weightx = 0.1;
    c.weighty = 0.0;
    c.fill = GridBagConstraints.NONE;
    c.anchor = GridBagConstraints.WEST;

    add(new JLabel(rc.getString("settingseditor.workday.lower.boundary")), c);
    c.gridy++;
    add(new JLabel(rc.getString("settingseditor.workday.upper.boundary")), c);
    c.gridy++;
    add(new JLabel(rc.getString("settingseditor.workday.slot.height")), c);

    c.gridy = 0;
    c.gridx = 1;
    c.weightx = 1.0;
    c.anchor = GridBagConstraints.EAST;
    add(workDayStart, c);
    c.gridy++;
    add(workDayEnd, c);
    c.gridy++;
    txtSlotHeight = new JTextField(3);
    add(txtSlotHeight, c);

    c.gridy++;
    c.gridx = 0;
    c.weightx = 0.1;
    c.weighty = 0.0;
    c.fill = GridBagConstraints.NONE;
    c.anchor = GridBagConstraints.WEST;

    add(new JLabel(rc.getString("settingseditor.workday.color1")), c);
    c.gridy++;
    add(new JLabel(rc.getString("settingseditor.workday.color2")), c);
    c.gridy--;

    c.gridx = 1;
    c.weightx = 1.0;
    c.fill = GridBagConstraints.HORIZONTAL;

    btnChooseWorkDayColor = new JButton(rc
        .getString("settingseditor.workday.changecolor"));
    btnChooseWorkDayColor.addActionListener(this);
    btnChooseWorkDayColor.setBackground(VerdandiModel.getConf()
        .getWorkdayEditorColor());
    add(btnChooseWorkDayColor, c);
    c.gridy++;

    btnChooseWorkDayAlternativeColor = new JButton(rc
        .getString("settingseditor.workday.changecolor"));
    btnChooseWorkDayAlternativeColor.addActionListener(this);
    btnChooseWorkDayAlternativeColor.setBackground(VerdandiModel.getConf()
        .getWorkdayEditorAlternativeColor());
    add(btnChooseWorkDayAlternativeColor, c);

    initFromConfig();

    setBorder(BorderFactory.createTitledBorder(rc
        .getString("settingseditor.workday.frame.label")));

  }

  public void actionPerformed(ActionEvent evt) {
    if (evt.getSource() == btnChooseWorkDayColor) {
      Color c = JColorChooser.showDialog(this, rc
          .getString("colorchooser.title"), VerdandiModel.getConf()
          .getWorkdayEditorColor());
      if (c != null) {
        btnChooseWorkDayColor.setBackground(c);
      }
    } else if (evt.getSource() == btnChooseWorkDayAlternativeColor) {
      Color c = JColorChooser.showDialog(this, rc
          .getString("colorchooser.title"), VerdandiModel.getConf()
          .getWorkdayEditorAlternativeColor());
      if (c != null) {
        btnChooseWorkDayAlternativeColor.setBackground(c);
      }
    }

  }

  public void commit() {
    int lower = Integer.parseInt((String) workDayStart.getSelectedItem());
    int upper = Integer.parseInt((String) workDayEnd.getSelectedItem());
    int slotHeight = VerdandiModel.getConf().getWorkDaySlotHeight();
    try {
      slotHeight = Integer.parseInt(txtSlotHeight.getText());
    } catch (NumberFormatException e) {
    }
    VerdandiModel.getConf().setWorkDayParameter(lower, upper, slotHeight);

    conf.setWorkdayEditorColor(btnChooseWorkDayColor.getBackground());
    conf.setWorkdayEditorAlternativeColor(btnChooseWorkDayAlternativeColor
        .getBackground());

  }

  private void initFromConfig() {
    String start = dcfmt.format(VerdandiModel.getConf().getWorkDayFirstHour());
    String end = dcfmt.format(VerdandiModel.getConf().getWorkDayLastHour());
    workDayStart.setSelectedItem(start);
    workDayEnd.setSelectedItem(end);
    txtSlotHeight.setText(Integer.toString(VerdandiModel.getConf()
        .getWorkDaySlotHeight()));
    btnChooseWorkDayColor.setBackground(conf.getWorkdayEditorColor());
    btnChooseWorkDayAlternativeColor.setBackground(conf
        .getWorkdayEditorAlternativeColor());
  }

  @Override
  public void reset() {
    initFromConfig();
    repaint();
  }

}
