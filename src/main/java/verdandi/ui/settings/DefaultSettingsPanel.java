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
 * Created on 04.12.2006
 * Author: osebelin
 *
 */
package verdandi.ui.settings;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Map.Entry;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import verdandi.DurationFormatter;
import verdandi.persistence.PersistenceConfigurationDescription;
import verdandi.ui.report.AnnotatedWorkRecordView;

public class DefaultSettingsPanel extends SettingsPanel {

  private static final long serialVersionUID = 1L;

  private static final Log LOG = LogFactory.getLog(DefaultSettingsPanel.class);

  private static final ResourceBundle RC = ResourceBundle
      .getBundle("TextResources");

  private JRadioButton radioFormatHHMM;

  private JRadioButton radioFormatHHQuarters;

  private JCheckBox storePasswordCheckBox, showTimerOnStartupCheckBox,
      initAnnotatedWorkRecordsOnStartup;

  // Child panel
  private WorkDaySettingsPanel workDaySettingsPanel;

  private Map<String, JTextField> persistenceFields = new HashMap<String, JTextField>();

  /**
   * @param model
   */
  public DefaultSettingsPanel() {
    super();
    initControls();
    reset();
  }

  private void initControls() {
    setLayout(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();
    c.insets = new Insets(5, 5, 5, 5);
    c.gridx = c.gridy = 0;
    c.weightx = 1.0;
    c.weighty = 0.0;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.anchor = GridBagConstraints.WEST;

    add(getMiscellaneousSettingsPanel(), c);
    c.gridy++;
    add(getTimeFormatSettingsPanel(), c);
    c.gridy++;
    workDaySettingsPanel = new WorkDaySettingsPanel();
    add(workDaySettingsPanel, c);
    c.gridy++;
    add(getAnnotatedWorkRecordViewPrefs(), c);
    c.gridy++;
    add(getPersistenceSettingsPanel(), c);
  }

  private JPanel getTimeFormatSettingsPanel() {
    JPanel res = new JPanel();
    res.setLayout(new BoxLayout(res, BoxLayout.LINE_AXIS));

    radioFormatHHMM = new JRadioButton("01:45");
    radioFormatHHQuarters = new JRadioButton("1,75");

    ButtonGroup grp = new ButtonGroup();
    grp.add(radioFormatHHMM);
    grp.add(radioFormatHHQuarters);

    res.add(radioFormatHHMM);
    res.add(Box.createHorizontalStrut(5));
    res.add(radioFormatHHQuarters);

    res.setBorder(BorderFactory.createTitledBorder(RC
        .getString("settingseditor.timeformat.title")));
    return res;
  }

  private JPanel getMiscellaneousSettingsPanel() {
    JPanel res = new JPanel();
    res.setLayout(new BoxLayout(res, BoxLayout.PAGE_AXIS));

    showTimerOnStartupCheckBox = new JCheckBox(RC
        .getString("settingseditor.show.timer.on.startup"));

    storePasswordCheckBox = new JCheckBox(RC
        .getString("settingseditor.store.password.title"));

    res.add(showTimerOnStartupCheckBox);
    res.add(storePasswordCheckBox);

    return res;
  }

  private JPanel getPersistenceSettingsPanel() {
    JPanel res = new JPanel(new GridBagLayout());
    res.setBorder(BorderFactory.createTitledBorder(RC
        .getString("settingseditor.persistence.title")));

    GridBagConstraints c = new GridBagConstraints();
    c.insets = new Insets(5, 5, 5, 5);
    c.gridy = 0;
    c.weighty = 0.0;
    c.anchor = GridBagConstraints.WEST;

    PersistenceConfigurationDescription pdc = conf
        .getPersistenceConfigurationDescription();
    if (pdc == null) {
      return res;
    }

    for (String key : pdc.getOrderedKeys()) {
      c.weightx = 0.1;
      c.gridx = 0;
      c.fill = GridBagConstraints.NONE;
      JLabel lbl = new JLabel(pdc.get(key).getName());
      lbl.setToolTipText(pdc.get(key).getDescription());
      res.add(lbl, c);
      c.gridx = 1;
      c.weightx = 1.0;
      c.fill = GridBagConstraints.HORIZONTAL;
      JTextField txt = new JTextField(20);
      txt.setText(conf.getString(pdc.get(key).getConfigKey()));
      res.add(txt, c);
      persistenceFields.put(pdc.get(key).getConfigKey(), txt);
      c.gridy++;
    }

    return res;
  }

  public void storePrefs() {
    Preferences prefs = Preferences.userNodeForPackage(DurationFormatter.class);

    if (radioFormatHHQuarters.isSelected()) {
      prefs.putInt(DurationFormatter.PREF_DISPLAY_MODE,
          DurationFormatter.DISPLAY_DURATION_HH_QUARTER);
    } else if (radioFormatHHMM.isSelected()) {
      prefs.putInt(DurationFormatter.PREF_DISPLAY_MODE,
          DurationFormatter.DISPLAY_DURATION_HHMM);
    }

    try {
      prefs.flush();
    } catch (BackingStoreException e) {
      LOG.error("Cannot store Prefs: ", e);
    }
    conf.setStorePasswd(storePasswordCheckBox.isSelected());
    conf.setShowTimerOnStartup(showTimerOnStartupCheckBox.isSelected());

    for (Entry<String, JTextField> pt : persistenceFields.entrySet()) {
      conf.setConfigProperty(pt.getKey(), pt.getValue().getText());
    }

    conf.setConfigProperty(AnnotatedWorkRecordView.PREF_RESTORE_ON_INIT,
        Boolean.toString(initAnnotatedWorkRecordsOnStartup.isSelected()));

    workDaySettingsPanel.commit();

  }

  private JPanel getAnnotatedWorkRecordViewPrefs() {
    JPanel res = new JPanel();
    res.setBorder(BorderFactory.createTitledBorder(RC
        .getString("settingseditor.annotatedworkview.title")));

    res.setLayout(new BoxLayout(res, BoxLayout.LINE_AXIS));
    initAnnotatedWorkRecordsOnStartup = new JCheckBox(RC
        .getString("settingseditor.annotatedworkview.doinitonstartup"));

    boolean selected = conf.getString(
        AnnotatedWorkRecordView.PREF_RESTORE_ON_INIT, "false")
        .equalsIgnoreCase("true");
    initAnnotatedWorkRecordsOnStartup.setSelected(selected);

    res.add(Box.createHorizontalStrut(5));
    res.add(initAnnotatedWorkRecordsOnStartup);
    res.add(Box.createHorizontalGlue());
    return res;
  }

  @Override
  public void commit() {
    storePrefs();
  }

  @Override
  public void reset() {
    Preferences prefs = Preferences.userNodeForPackage(DurationFormatter.class);
    int displayMode = prefs.getInt(DurationFormatter.PREF_DISPLAY_MODE,
        DurationFormatter.DISPLAY_DURATION_HHMM);
    if (displayMode == DurationFormatter.DISPLAY_DURATION_HHMM) {
      radioFormatHHMM.setSelected(true);
    } else {
      radioFormatHHQuarters.setSelected(true);
    }
    storePasswordCheckBox.setSelected(conf.isStorePasswd());
    showTimerOnStartupCheckBox.setSelected(conf.isShowTimerOnStartup());
    for (Entry<String, JTextField> pt : persistenceFields.entrySet()) {
      pt.getValue().setText(conf.getConfigProperty(pt.getKey()));
    }
    workDaySettingsPanel.reset();
  }

}
