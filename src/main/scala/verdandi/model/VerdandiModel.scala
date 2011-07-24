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

import verdandi.model.persistence.WorkRecordStorage
import verdandi.{ VerdandiConfiguration => OldCFG }
import verdandi.model.persistence.SerializedFilePersistence
import verdandi.model.persistence.CostUnitUserStorage
import verdandi.model.persistence.VerdandiPersistenceWrapper
import verdandi.event.EventBoard
import verdandi.persistence.PersistenceException
import verdandi.plugin.CurrentWeekPlugin
import verdandi.event.VerdandiEventListener
import java.util.Calendar
import verdandi.event._
import java.util.Date
import scala.collection.mutable.ListBuffer
import scala.collection.mutable.LinkedList
import scala.reflect.BeanProperty
import verdandi.plugin.PluginLoader
import javax.swing.JFrame
import verdandi.persistence.VerdandiPersistence
import java.util.{ List => JList }
import scala.collection.JavaConversions._
import com.weiglewilczek.slf4s.Logging

object VerdandiModel extends Logging {

  private class ShutdownHook extends Thread {
    override def run() = shutdown()
  }
  Runtime.getRuntime().addShutdownHook(new ShutdownHook);

  // FIXME: Add this as listener
  @deprecated
  @BeanProperty
  val conf: OldCFG = new OldCFG()

  @deprecated
  @BeanProperty
  lazy val persistence: VerdandiPersistence = new VerdandiPersistenceWrapper()

  val costUnitUserStorage: CostUnitUserStorage = SerializedFilePersistence

  val workRecordStorage: WorkRecordStorage = SerializedFilePersistence

  private val quitListeners = new ListBuffer[VerdandiListener]()

  private val listeners = new ListBuffer[VerdandiEventListener]()

  private lazy val legacyEventBoard = new EventBoard

  @BeanProperty
  val currentWeekModel = new CurrentWeekModel()

  // FIXME: Looks ugly.
  @BeanProperty
  var defaultParentFrame: JFrame = _

  @BeanProperty
  lazy val pluginLoader: PluginLoader = new PluginLoader()

  def addListener(listener: VerdandiListener) = quitListeners += listener

  /**
   * @param listener
   * @param eventType
   * @return
   * @deprecated use EventBroadcaster
   */
  @Deprecated
  def addListener(listener: VerdandiEventListener, eventType: Class[_]) = {
    if (listeners.contains(listener)) {
      logger.warn("Listener already registered: " + listener)
    } else {
      logger.debug("Adding listsener: " + listener)
      listeners += listener
    }
  }

  @throws(classOf[PersistenceException])
  def getTimeSlots(day: Date): java.util.List[WorkRecord] = {
    val cal = Calendar.getInstance();
    cal.setTime(day);
    cal.set(Calendar.HOUR_OF_DAY, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);
    val _start = cal.getTime();
    cal.set(Calendar.HOUR_OF_DAY, 23);
    cal.set(Calendar.MINUTE, 59);
    cal.set(Calendar.SECOND, 59);
    cal.set(Calendar.MILLISECOND, 0);
    val _end = cal.getTime();
    return persistence.getWorkRecords(_start, _end);
  }

  def fireEvent(evt: VerdandiEvent) = {
    listeners.foreach({ l =>
      logger.debug("Notifying " + l + " about " + evt)
      l.eventOccured(evt)
    })
  }

  def getCurrentWeekPlugins(): JList[CurrentWeekPlugin] = {
    getPluginLoader.getPlugins
      .filter(_.isInstanceOf[CurrentWeekPlugin])
      .map(_.asInstanceOf[CurrentWeekPlugin]).toList
  }

  // Was provided by original model
  @throws(classOf[PersistenceException])
  def initProxiedPersistence() {
    persistence.init(conf, legacyEventBoard)
  }

  /**
   *
   * Called upon application shutdown!
   *
   * @see ShutDownHook
   */
  def shutdown() = {
    EventBroadcaster.publish(ApplicationShutdown())
    quitListeners.foreach(_.applicationQuit())
  }
}

