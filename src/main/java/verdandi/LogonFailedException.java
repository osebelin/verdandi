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
 * Created on 11.11.2005
 * Author: osebelin
 *
 */
package verdandi;

import verdandi.persistence.PersistenceException;

/**
 * FIXME: why a now nexception?
 * 
 * Thrown, to indicate, that a logon has failed
 */
public class LogonFailedException extends PersistenceException {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public LogonFailedException(Throwable cause) {
    super(ErrorCode.LOGON_FAILED, cause);
  }

  public LogonFailedException() {
    super(ErrorCode.LOGON_FAILED);
  }

}
