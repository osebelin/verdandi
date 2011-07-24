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

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ErrorCode {

  private static final Log LOG = LogFactory.getLog(ErrorCode.class);

  private static ResourceBundle errTrans = ResourceBundle.getBundle("errcodes");

  public static final ErrorCode SAVE_PROJECT_FAILED = new ErrorCode(
      "save_project_failed");

  public static final ErrorCode SAVE_WORK_RECORD_FAILED = new ErrorCode(
      "save_timeslot_failed");

  public static final ErrorCode SAVE_USER_FAILED = new ErrorCode(
      "save_user_failed");

  public static final ErrorCode GET_WORK_RECORD_FAILED = new ErrorCode(
      "get_work_record_failed");

  public static final ErrorCode GET_USERS_FAILED = new ErrorCode(
      "get_users_failed");

  public static final ErrorCode GET_PROJECTS_FAILED = new ErrorCode(
      "get_projects_failed");

  public static final ErrorCode DELETE_TIMESLOT_FAILED = new ErrorCode(
      "delete_timeslot_failed");

  public static final ErrorCode DB_QUERY_FAILED = new ErrorCode(
      "db_query_failed");

  public static final ErrorCode LOGON_FAILED = new ErrorCode("db_logon_failed");

  public static final ErrorCode INIT_FAILED = new ErrorCode("init_failed");

  public static final ErrorCode PERSISTENCE_ERROR = new ErrorCode(
      "persistence_error");

  public static final ErrorCode DUPLICATE_PROJECT = new ErrorCode(
      "duplicate_project");

  public static final ErrorCode NO_SUCH_USER = new ErrorCode("no_such_user");

  public static final ErrorCode TRANSPORT_ERROR = new ErrorCode(
      "transport_error");

  public static final ErrorCode SERVER_UNAVALIABLE = new ErrorCode(
      "server_unavailable");

  public static final ErrorCode EXPORT_ERROR = new ErrorCode("export_error");

  public static final ErrorCode IO_ERROR = new ErrorCode("io_error");

  public static final ErrorCode PLUGIN_ERROR = new ErrorCode("plugin_error");

  private String errcode = "";

  /**
   * @param errcode
   */
  private ErrorCode(String errcode) {
    super();
    this.errcode = errcode;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    try {
      return errTrans.getString(errcode);
    } catch (MissingResourceException e) {
      LOG.error("Unresolved error code: " + errcode);
      return errcode;
    }
  }

}
