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
 * Created on 12.03.2005
 * Author: osebelin
 *
 */
package verdandi;


public class VerdandiException extends Exception {

	private static final long serialVersionUID = 3833182514972864816L;

    private ErrorCode errorCode;
    
	/**
	 * @param message
	 * @param cause
	 */
	public VerdandiException(ErrorCode errCode, String message, Throwable cause) {
		super(message, cause);
        errorCode = errCode;
	}

  /**
   * @param message
   * @param cause
   */
  public VerdandiException(ErrorCode errCode, String message) {
    super(message);
    errorCode = errCode;
  }
	
	/**
	 * @param message
	 */
	public VerdandiException(ErrorCode errCode, Throwable cause) {
		super(cause);
        errorCode = errCode;
	}
	
	/**
	 * @param message
	 */
	public VerdandiException(ErrorCode errCode) {
		super(errCode.toString());
        errorCode = errCode;
	}

  public ErrorCode getErrorCode() {
    return errorCode;
  }

    
}
