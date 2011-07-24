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
package verdandi.model

import java.util.Date
import java.io.{ File, FileInputStream, FileOutputStream }
import java.util.Properties
import verdandi.{ RichCalendar, VerdandiHelper }
import verdandi.event._
import scala.swing.Reactor
import com.weiglewilczek.slf4s.Logging

object VerdandiConfiguration extends Reactor with Logging {

  val basedir = new File(System.getProperty("user.home"), ".verdandi")
  private val configFile = new File(basedir, "config")
  private val props = new Properties()

  init()

  private def init() {
    if (!basedir.exists) basedir.mkdir
    logger.info("Loading configuration from " + configFile.getAbsolutePath)
    if (configFile.isFile) {
      VerdandiHelper.withCloseable(new FileInputStream(configFile)) { is =>
        props.load(is)
      }
    }
    listenTo(EventBroadcaster)
    reactions += {
      case shuttdown: ApplicationShutdown => {
        VerdandiHelper.withCloseable(new FileOutputStream(configFile)) { os =>
          props.store(os, "Verdandi configuration.")
        }
      }
    }
  }

  def startHidden: Boolean = props.getProperty("startHidden", "false").toBoolean
  def startHidden_=(_startHidden: Boolean) = props.setProperty("startHidden", _startHidden.toString)

  def startTrackingOnLoad: Boolean = props.getProperty("startTrackingOnLoad", "false").toBoolean
  def startTrackingOnLoad_=(_startTracking: Boolean) = props.setProperty("startTrackingOnLoad", _startTracking.toString)

  def costUnitToTrackOnLoad: String = props.getProperty("costUnitToTrackOnLoad", "")
  def costUnitToTrackOnLoad_=(_costUnitId: String) = props.setProperty("costUnitToTrackOnLoad", _costUnitId.toString)

  def exitOnClose: Boolean = props.getProperty("exitOnClose", "true").toBoolean
  def exitOnClose_=(_exitOnClose: Boolean) = props.setProperty("exitOnClose", _exitOnClose.toString)

  def reportFrom: Date = new Date(props.getProperty("reportFrom", "0").toLong)
  def reportFrom_=(d: Date) = props.setProperty("reportFrom", d.getTime.toString)

  def reportUntil: Date = new Date(props.getProperty("reportUntil", System.currentTimeMillis.toString).toLong)
  def reportUntil_=(d: Date) = props.setProperty("reportUntil", d.getTime.toString)

  /**
   * @return The resolution, i.e. the minimum length of a work record.
   */
  val timeResolution = 15

  object WorkDayEditorConfiguration {

    def firstHour = props.getProperty("firstHour", "8").toInt
    def lastHour = props.getProperty("lastHour", "22").toInt

    def firstHour_=(h: Int) = props.setProperty("firstHour", h.toString)
    def lastHour_=(h: Int) = props.setProperty("lastHour", h.toString)

    def minuteGranularity = timeResolution

    def pixelsPerSlot = 16

    /** the calculated minimum height based on the configuration */
    def minHeight: Int = (lastHour - firstHour) * 60 / minuteGranularity * pixelsPerSlot
    def lowerBound = RichCalendar().hhmm(firstHour, 0)
    def upperBound = RichCalendar().hhmm(lastHour, 0)
  }

}

