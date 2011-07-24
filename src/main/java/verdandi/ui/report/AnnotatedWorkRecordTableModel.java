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

import java.text.DateFormat;
import java.util.ResourceBundle;

import verdandi.DurationFormatter;
import verdandi.event.ErrorEvent;
import verdandi.model.VerdandiModel;
import verdandi.model.WorkRecord;
import verdandi.persistence.PersistenceException;
import verdandi.ui.common.WidthStoringTableModel;

public class AnnotatedWorkRecordTableModel extends WidthStoringTableModel
    implements AnnotatedWorkReportModelListener {

  private static final long serialVersionUID = 1L;

  private static final ResourceBundle RC = ResourceBundle
      .getBundle("TextResources");

  private AnnotatedWorkReportModel parentModel;

  private DateFormat dfmt = DateFormat.getDateInstance(DateFormat.MEDIUM);

  protected AnnotatedWorkRecordTableModel(AnnotatedWorkReportModel parentModel) {
    super(RC);
    this.parentModel = parentModel;
    this.parentModel.addListener(this);
  }

  @Override
  protected String[] getColumnNames() {
    return new String[] { "annotatedworkrecord.table.column.project.label",
        "annotatedworkrecord.table.column.date.label",
        "annotatedworkrecord.table.column.duration.label",
        "annotatedworkrecord.table.column.annotation.label" };

  }

  @Override
  public int getRowCount() {
    return parentModel.getRecords().size();
  }

  @Override
  public Object getValueAt(int rowIndex, int columnIndex) {

    WorkRecord record = parentModel.getRecords().get(rowIndex);
    switch (columnIndex) {
    case 0:
      return record.getAssociatedProject().getId();
    case 1:
      return dfmt.format(record.getStartTime());
    case 2:
      return DurationFormatter.getFormatter().format(record.getDuration());
    case 3:
      return record.getAnnotation();
    default:
      throw new IndexOutOfBoundsException("" + columnIndex);
    }
  }

  /**
   * @return the parentModel
   */
  public AnnotatedWorkReportModel getParentModel() {
    return parentModel;
  }

  @Override
  public void recordsChanged() {
    fireTableDataChanged();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isCellEditable(int rowIndex, int columnIndex) {
    return columnIndex == 3;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setValueAt(Object value, int rowIndex, int columnIndex) {
    if (columnIndex != 3) {
      throw new IllegalArgumentException("Column not editable: " + columnIndex);
    }

    String annotation;
    if ((value instanceof String)) {
      annotation = (String) value;
    } else {
      annotation = value.toString();
    }

    WorkRecord record = parentModel.getRecords().get(rowIndex);
    record.setAnnotation(annotation);
    try {
      VerdandiModel.getPersistence().save(record);
    } catch (PersistenceException e) {
      VerdandiModel.fireEvent(new ErrorEvent(e));
    }
  }

}
