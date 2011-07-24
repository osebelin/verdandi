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
 * Created on 02.11.2006
 * Author: osebelin
 *
 */
package verdandi;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Date;
import java.util.Calendar;

import org.apache.log4j.Logger;

public class Helper {

  private static Logger log = Logger.getLogger(Helper.class);

  private static Calendar cal = Calendar.getInstance();

  /**
   * java.sql.Date is braindamage. It should only wrap around a date, but
   * considers whole time for equals, so 2006-11-02 != 2006-11-02
   * 
   * @param d
   *          a day
   * @return a day with h,m,s,ms set to 0.
   */
  public static Date normalize(Date d) {
    synchronized (cal) {
      cal.setTime(d);
      cal.set(Calendar.HOUR_OF_DAY, 0);
      cal.set(Calendar.MINUTE, 0);
      cal.set(Calendar.SECOND, 0);
      cal.set(Calendar.MILLISECOND, 0);
      return new Date(cal.getTimeInMillis());
    }
  }

  /**
   * Creates a new, normalized date.
   * 
   * @return A new day with h,m,s,ms set to 0.
   */
  public static Date newDay() {
    return normalize(new Date(System.currentTimeMillis()));
  }

  public static void close(Closeable close) {
    if (close != null) {
      try {
        close.close();
      } catch (IOException e) {
        log.error("", e);
      }
    }
  }

}
