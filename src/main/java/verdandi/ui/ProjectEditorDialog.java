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
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.JDialog;
import javax.swing.JFrame;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import verdandi.event.ErrorEvent;
import verdandi.model.CostUnit;
import verdandi.model.VerdandiModel;
import verdandi.persistence.PersistenceException;
import verdandi.ui.common.OKCancelPanel;

@SuppressWarnings("serial")
public class ProjectEditorDialog extends JDialog implements ActionListener {

  private static final Log LOG = LogFactory.getLog(ProjectEditorDialog.class);

  private static ResourceBundle rb = ResourceBundle.getBundle("TextResources");

  private CostUnit project;

  private ProjectEditorPanel editorPanel;

  /**
   * @param project
   * @param model
   * @throws HeadlessException
   */
  public ProjectEditorDialog(JFrame owner, CostUnit project)
      throws HeadlessException {
    super(owner, rb.getString("projecteditor.title"));
    if (owner != null) {
      setModal(true);
    }
    this.project = project;
    initControls();
    pack();
  }

  private void initControls() {

    getContentPane().setLayout(new BorderLayout());
    editorPanel = new ProjectEditorPanel(project);
    getContentPane().add(editorPanel, BorderLayout.CENTER);
    getContentPane().add(new OKCancelPanel(this), BorderLayout.SOUTH);
  }

  public void actionPerformed(ActionEvent evt) {
    if (evt.getActionCommand() == OKCancelPanel.CMD_OK) {
      if (editorPanel.validateEntries()) {
        LOG.info("Storing project " + project);
        try {
          editorPanel.updateProject();
          VerdandiModel.getPersistence().save(project);
          this.setVisible(false);
        } catch (PersistenceException ex) {
          LOG.error("", ex);
          VerdandiModel.fireEvent(new ErrorEvent(ex));
          editorPanel.resetProject();
        }
      } else {
        LOG.info("Editor validation failed. Not storing project " + project);
      }

    } else if (evt.getActionCommand() == OKCancelPanel.CMD_CANCEL) {
      LOG.debug("Cancelled");
      this.setVisible(false);
    }

  }

}
