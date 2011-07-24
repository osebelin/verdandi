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
package verdandi.model.persistence

import verdandi.event.PersonalCostUnitSelectionChanged
import verdandi.event.WorkRecordEvent
import verdandi.event.CostUnitEvent
import scala.collection.mutable.ListBuffer
import verdandi.event.EventBroadcaster
import scala.swing.Reactor
import verdandi.persistence.PersistenceConfigurationDescription
import verdandi.persistence.PersistenceListener
import verdandi.persistence.SaveProgressMonitor
import verdandi.persistence.VerdandiPersistence
import verdandi.SummaryItem
import verdandi.model.CostUnit
import verdandi.model.WorkRecord
import java.util.Date
import verdandi.event.EventBoard
import verdandi.VerdandiConfiguration
import java.util.{ List => JList, ArrayList => JArrayList, Map => JMap, Set => JSet, HashSet => JHashSet, Collection => JCollection }
import scala.collection.JavaConversions._
import com.weiglewilczek.slf4s.Logging

/**
 * Wraps around a new persistence implementation
 * Should ease migration
 * @author osebelin
 *
 */
class VerdandiPersistenceWrapper extends VerdandiPersistence with Reactor with Logging {

  // FIXME: Events!
  val costUnitStorage: CostUnitUserStorage = SerializedFilePersistence
  val costUnitEditorStorage: CostUnitEditorStorage = SerializedFilePersistence
  val recordStorage: WorkRecordStorage = SerializedFilePersistence

  private val legacyListeners = new ListBuffer[PersistenceListener]()

  // listen to persistence events to forward them to the legay listeners.
  listenTo(EventBroadcaster)

  reactions += {
    case e: CostUnitEvent => legacyListeners.foreach(_.projectChanged(e.costUnit))
    case e: WorkRecordEvent => legacyListeners.foreach(_.workRecordChanged(e.workRecord))
    case e: PersonalCostUnitSelectionChanged => legacyListeners.foreach(_.ownProjectSelectionChanged())
  }

  def addListener(listener: PersistenceListener) = legacyListeners += listener

  def removeListener(listener: PersistenceListener) = legacyListeners -= listener

  def init(conf: VerdandiConfiguration, board: EventBoard) {
    legacyListeners.foreach(_.persistenceInit())
  }

  /**
   * @param day
   * @return A List of TimeSlots
   * @throws PersistenceException
   *
   */
  def getWorkRecords(from: Date, to: Date): JList[WorkRecord] = {
    recordStorage.getWorkRecords(from, to)
  }

  /**
   * Saves a timeslot (INSERT or UPDATE)
   *
   * @param workRecord
   * @throws PersistenceException
   */
  def save(workRecord: WorkRecord) {
    recordStorage.save(workRecord)
  }

  /**
   * @return Returns the projects.
   * @throws PersistenceException
   */
  def getProjects(): JMap[String, CostUnit] = {
    logger.debug("getProjects")
    val res = new scala.collection.mutable.HashMap[String, CostUnit]()
    costUnitEditorStorage.getCostUnits.foreach(cu => res += (cu.getId -> cu))
    res
  }

  def getOwnProjectSelection(): JSet[String] = {
    logger.debug("getOwnProjectSelection")
    val res = new JHashSet[String]()
    costUnitStorage.getOwnSelection.foreach(cu => res.add(cu.getId))
    res
  }

  def storeOwnProjectSelection(selection: JSet[String]) {
    logger.debug("storeOwnProjectSelection")
    costUnitStorage.setOwnSelection(selection.toSet)
  }

  /**
   * Intersection of active projects and own projects.
   *
   * @return
   * @throws PersistenceException
   */
  def getOwnActiveProjects(): JList[CostUnit] = {
    logger.debug("getOwnActiveProjects")
    costUnitStorage.getOwnSelection
  }

  def getActiveProjectList(): JList[CostUnit] = {
    logger.debug("getActiveProjectList")
    costUnitStorage.getActiveCostUnits
  }

  /**
   * Saves a StoredProject.
   *
   *
   * @param p
   * @throws PersistenceException
   */
  def save(costUnit: CostUnit) = costUnitEditorStorage.store(costUnit)

  def saveAll(projects: JCollection[CostUnit], monitor: SaveProgressMonitor) {
    monitor.start(projects.size)
    projects.foreach(cu => { costUnitEditorStorage.store(cu); monitor.next(cu.getId) })
    monitor.finished()
  }

  def delete(rec: WorkRecord) = recordStorage.delete(rec)

  /**
   * @param start
   * @param end
   * @param groupByAnnotation
   *          <code>true</code> to add a group by clause for the WorkRecords
   *          annotation
   * @return A List of
   * @throws PersistenceException
   */
  // TODO: add a boolean parameter: GroupByDescription!
  def getDurationSummaries(start: Date, end: Date, groupByAnnotation: Boolean): JList[SummaryItem] =
    new JArrayList[SummaryItem](recordStorage getDurationSummaries (start, end, groupByAnnotation))

  def getConfigurationDescription(): PersistenceConfigurationDescription = null

}
