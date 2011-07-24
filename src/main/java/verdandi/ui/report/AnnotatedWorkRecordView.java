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
package verdandi.ui.report;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jdesktop.swingx.JXDatePicker;

import verdandi.DurationFormatter;
import verdandi.model.CostUnit;
import verdandi.model.VerdandiListener;
import verdandi.model.VerdandiModel;
import verdandi.ui.JXDatePickerFactory;

public class AnnotatedWorkRecordView implements ActionListener,
    VerdandiListener, AnnotatedWorkReportModelListener, ListSelectionListener {

  private static final String PREF_PROJECT = "project";

  private static final String PREF_FROM = "from";

  private static final String PREF_TO = "to";

  public static final String PREF_RESTORE_ON_INIT = "restoreOnInit";

  private static final ResourceBundle RC = ResourceBundle
      .getBundle("TextResources");

  protected JPanel view;

  protected AnnotatedWorkReportModel model;

  protected JXDatePicker from, to;

  protected JList projectSelector;

  protected JLabel durationLabel;

  private static final String TOTAL = RC.getString("summary.table.total");

  public AnnotatedWorkRecordView() {
    super();
    VerdandiModel.addListener(this);
    model = new AnnotatedWorkReportModel();
    buildView();
    model.addListener(this);
  }

  private void buildView() {
    view = new JPanel(new BorderLayout());
    view.add(buildTopPanel(), BorderLayout.NORTH);
    view.add(buildTablePanel(), BorderLayout.CENTER);
    view.add(buildBottomPanel(), BorderLayout.SOUTH);
  }

  private void initPreferences() {
    Preferences prefs = Preferences.userNodeForPackage(getClass());

    String defaultProject = prefs.get(PREF_PROJECT, "");
    String defaultFrom = prefs.get(PREF_FROM, "");
    String defaultTo = prefs.get(PREF_TO, "");

    // FIXME: Disabled
    // if (!defaultProject.equals("")) {
    // for (int i = 0; i < projectSelector.getItemCount(); i++) {
    // Project p = (Project) projectSelector.getItemAt(i);
    // if (p.getId().equals(defaultProject)) {
    // projectSelector.setSelectedIndex(i);
    // break;
    // }
    // }
    // }
    if (!defaultFrom.equals("")) {
      long fromVal = Long.parseLong(defaultFrom);
      model.setIntervalStart(new Date(fromVal));
      from.setDate(new Date(fromVal));
    }
    if (!defaultTo.equals("")) {
      long toVal = Long.parseLong(defaultTo);
      model.setIntervalEnd(new Date(toVal));
      to.setDate(new Date(toVal));
    }

  }

  private void storePrefs() {
    Preferences prefs = Preferences.userNodeForPackage(getClass());

    // FIXME: Disabeld
    // if (projectSelector.getSelectedItem() != null) {
    // Project p = (Project) projectSelector.getSelectedItem();
    // prefs.put(PREF_PROJECT, p.getId());
    // }
    prefs.put(PREF_FROM, Long.toString(from.getDate().getTime()));
    prefs.put(PREF_TO, Long.toString(to.getDate().getTime()));
  }

  private JPanel buildBottomPanel() {
    JPanel res = new JPanel();
    res.setLayout(new BoxLayout(res, BoxLayout.LINE_AXIS));
    res.add(Box.createHorizontalGlue());

    durationLabel = new JLabel(TOTAL
        + DurationFormatter.getFormatter().format(model.getDurationSum()));

    Font org = durationLabel.getFont();
    Font bold = new Font(org.getFontName(), Font.BOLD, org.getSize());
    durationLabel.setFont(bold);

    res.add(durationLabel);
    res.add(Box.createHorizontalStrut(25));

    return res;
  }

  private JPanel buildTopPanel() {

    JPanel pSelectorPanel = new JPanel();
    pSelectorPanel
        .setLayout(new BoxLayout(pSelectorPanel, BoxLayout.LINE_AXIS));
    pSelectorPanel.add(Box.createHorizontalGlue());
    pSelectorPanel.add(new JLabel(RC
        .getString("annotatedworkrecord.control.project.label")));
    pSelectorPanel.add(Box.createHorizontalStrut(5));

    projectSelector = new JList(new ProjectSelectorModel(model));
    pSelectorPanel.add(projectSelector);
    pSelectorPanel.add(Box.createHorizontalStrut(20));

    projectSelector.addListSelectionListener(this);

    JPanel datePanel = new JPanel();
    datePanel.setLayout(new BoxLayout(datePanel, BoxLayout.LINE_AXIS));
    from = JXDatePickerFactory.getInstance().getDatePicker(
        model.getIntervalStart());
    from.addActionListener(this);
    to = JXDatePickerFactory.getInstance()
        .getDatePicker(model.getIntervalEnd());
    to.addActionListener(this);

    datePanel.add(new JLabel(RC
        .getString("annotatedworkrecord.control.from.label")));
    datePanel.add(Box.createHorizontalStrut(5));
    datePanel.add(from);
    datePanel.add(Box.createHorizontalStrut(20));
    datePanel.add(new JLabel(RC
        .getString("annotatedworkrecord.control.to.label")));
    datePanel.add(Box.createHorizontalStrut(5));
    datePanel.add(to);
    datePanel.add(Box.createHorizontalGlue());

    JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 5));
    topPanel.add(pSelectorPanel);
    topPanel.add(datePanel);
    topPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

    if (VerdandiModel.getConf()
        .getConfigProperty(PREF_RESTORE_ON_INIT, "false").equalsIgnoreCase(
            "true")) {
      initPreferences();
    }
    topPanel.invalidate();
    return topPanel;
  }

  private JPanel buildTablePanel() {
    JPanel tablePanel = new JPanel(new BorderLayout());
    AnnotatedWorkRecordTableModel tableModel = new AnnotatedWorkRecordTableModel(
        model);
    AnnotatedWorkRecordTable table = new AnnotatedWorkRecordTable(tableModel);
    tablePanel.add(new JScrollPane(table), BorderLayout.CENTER);
    return tablePanel;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == from) {
      model.setIntervalStart(from.getDate());
    } else if (e.getSource() == to) {
      model.setIntervalEnd(to.getDate());
    }
  }

  /**
   * @return the view
   */
  public JPanel getView() {
    return view;
  }

  @Override
  public void applicationQuit() {
    storePrefs();
  }

  @Override
  public void recordsChanged() {

    durationLabel.setText(TOTAL
        + DurationFormatter.getFormatter().format(model.getDurationSum()));
  }

  @Override
  public void valueChanged(ListSelectionEvent e) {
    List<CostUnit> sel = new ArrayList<CostUnit>();
    for (Object o : projectSelector.getSelectedValues()) {
      sel.add((CostUnit) o);
    }
    model.setSelectedProjects(sel);
  }
}
