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
 * Created on 04.12.2005
 * Author: osebelin
 *
 */
package verdandi.persistence;


import verdandi.ErrorCode;
import verdandi.VerdandiException;

public class PersistenceException extends VerdandiException{

  
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  /**
   * @param message
   * @param cause
   */
  public PersistenceException(String message, Throwable cause) {
    super(ErrorCode.PERSISTENCE_ERROR, message, cause);
  }

  /**
   * @param message
   */
  public PersistenceException(String message) {
    super(ErrorCode.PERSISTENCE_ERROR, message);
  }

  /**
   * @param errorCode
   * @param message
   */
  public PersistenceException(ErrorCode errorCode, String message) {
    super(errorCode, message);
  }
  /**
   * @param errorCode
   */
  public PersistenceException(ErrorCode errorCode) {
    super(errorCode);
  }
  
  
  /**
   * @param errCode
   * @param message
   */
  public PersistenceException(ErrorCode errCode, Throwable cause) {
    super(errCode, cause);
  } 

}
