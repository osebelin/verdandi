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
 * Created on 29.10.2006
 * Author: osebelin
 *
 */
package verdandi.ui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EtchedBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import verdandi.DurationFormatter;
import verdandi.model.SummaryViewModel;
import verdandi.model.VerdandiModel;
import verdandi.plugin.CurrentWeekPlugin;

@SuppressWarnings("serial")
public class SummaryPanel extends JPanel implements TableModelListener {

  private static final Log LOG = LogFactory.getLog(SummaryPanel.class);

  private static ResourceBundle RB = ResourceBundle.getBundle("TextResources");

  private static final ResourceBundle THEME_RC = ResourceBundle
      .getBundle("theme");

  private SummaryTable summaryTable;

  private SummaryViewModel tableModel;

  private JLabel lblTotal;

  private String total = RB.getString("summary.table.total");

  /**
   * @param model
   */
  public SummaryPanel() {
    super();
    initControls();
  }

  private JPanel getTopPanel() {
    JPanel res = new JPanel();

    GridBagConstraints c = new GridBagConstraints();
    c.insets = new Insets(5, 5, 5, 5);
    c.gridx = 0;
    c.gridy = 0;

    c.anchor = GridBagConstraints.EAST;
    c.fill = GridBagConstraints.NONE;
    c.weightx = 1.0;

    res.add(new WeekSelector(VerdandiModel.getCurrentWeekModel()));

    JMenuBar mnuBar = new JMenuBar();

    // JMenu menu = new JMenu(RB.getString("summary.view.menu.plugins.name"));
    JMenu menu = new JMenu();
    menu.setToolTipText(RB.getString("summary.view.menu.plugins.name"));

    URL imageURL = null;
    imageURL = Thread.currentThread().getContextClassLoader().getResource(
        THEME_RC.getString("icon.weekview.plugins"));
    ImageIcon pluginsIcon = new ImageIcon(imageURL, RB
        .getString("summary.view.menu.plugins.name"));

    menu.setIcon(pluginsIcon);

    List<CurrentWeekPlugin> pluginList = VerdandiModel.getCurrentWeekPlugins();
    for (CurrentWeekPlugin plugin : pluginList) {
      LOG.debug("Adding menu item from plugin "
          + plugin.getClass().getSimpleName());
      menu.add(plugin.getMenuEntry());
    }
    mnuBar.add(menu);
    // mnuBar.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
    mnuBar.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

    c.gridx++;
    c.anchor = GridBagConstraints.WEST;
    c.fill = GridBagConstraints.NONE;
    c.weightx = 0.1;
    res.add(mnuBar);

    c.gridx++;

    return res;
  }

  private void initControls() {
    setLayout(new BorderLayout());

    add(getTopPanel(), BorderLayout.NORTH);

    tableModel = new SummaryViewModel(SummaryViewModel.MODE_WEEK_OF_YEAR);

    summaryTable = new SummaryTable(tableModel);
    JScrollPane scrl = new JScrollPane(summaryTable);
    add(scrl, BorderLayout.CENTER);

    JPanel totalView = new JPanel();
    totalView.setLayout(new BoxLayout(totalView, BoxLayout.LINE_AXIS));

    lblTotal = new JLabel();

    lblTotal.setText(total
        + DurationFormatter.getFormatter().format(
            tableModel.getDurationTotalMinutes()));

    Font org = lblTotal.getFont();
    Font bold = new Font(org.getFontName(), Font.BOLD, org.getSize());
    lblTotal.setFont(bold);

    totalView.add(Box.createHorizontalGlue());
    totalView.add(lblTotal);
    totalView.add(Box.createHorizontalStrut(25));

    tableModel.addTableModelListener(this);

    add(totalView, BorderLayout.SOUTH);

  }

  public void tableChanged(TableModelEvent e) {
    lblTotal.setText(total
        + DurationFormatter.getFormatter().format(
            tableModel.getDurationTotalMinutes()));
  }

}
