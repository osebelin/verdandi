/**
 * *****************************************************************************
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
 * ****************************************************************************
 */
package verdandi.model.persistence

import scala.actors.Actor
import java.util.concurrent.atomic.AtomicLong
import java.io.{ File, FileInputStream, FileOutputStream, ObjectInputStream, ObjectOutputStream, Serializable }
import java.util.Date
import scala.collection.mutable.{ ListBuffer, HashMap }
import scala.swing.Reactor
import verdandi.{ VerdandiHelper }
import verdandi.event.{ EventBroadcaster, CostUnitChangedEvent, WorkRecordChangedEvent, WorkRecordDeletedEvent, PersonalCostUnitSelectionChanged, ApplicationShutdown }
import verdandi.model.{ CostUnit, WorkRecord, VerdandiModel }
import com.weiglewilczek.slf4s.Logging
import verdandi.model.SummaryItem

object SerializedFilePersistence extends AbstractUserStorage with CostUnitEditorStorage with Logging with Reactor {

  private val backend: StorageBackend = DeferredWriteFlatFileBackend

  var costUnits: ListBuffer[CostUnit] = backend.loadCostUnits()
  var ownSelections: ListBuffer[String] = backend.loadOwnSelection()
  var workRecords: ListBuffer[WorkRecord] = backend.loadWorkRecords()
  lazy val idGenerator: IdGenerator = backend.loadIdGenerator()

  var ownProjects: List[CostUnit] = _

  reactions += {
    case shutdown: ApplicationShutdown => backend.shutdown()
  }

  listenTo(EventBroadcaster)

  buildOwnSelection()

  private def newWorkRecordId: Int = {
    val res = idGenerator.workRecordId.getAndIncrement.toInt
    backend.writeIdGenerator(idGenerator)
    res
  }

  private def buildOwnSelection() {
    ownProjects = getActiveCostUnits.filter(cu => ownSelections.contains(cu.getId))
  }

  def getActiveCostUnits: List[CostUnit] = costUnits.filter(_.isActive).toList

  def store(c: CostUnit) = {
    costUnits = costUnits - c + c
    backend.writeCostUnits(costUnits)
    buildOwnSelection()
    EventBroadcaster.publish(CostUnitChangedEvent(c))
  }

  def inactivate(c: CostUnit) {
    logger.warn("FIXME: inactivate(CostUnit) not implemented. Fn needed?")
  }

  def activate(c: CostUnit) {
    logger.warn("FIXME: activate(CostUnit) not implemented. Fn needed?")
  }

  def getCostUnits: List[CostUnit] = costUnits.toList

  def costUnitById(id: String): CostUnit = {
    null
  }

  def getOwnSelection(): List[CostUnit] = ownProjects

  def addToOwnSelection(c: CostUnit) = {
    ownSelections = ownSelections - c.getId + c.getId
    backend
    backend.writeOwnSelection(ownSelections)
    buildOwnSelection()
    EventBroadcaster.publish(PersonalCostUnitSelectionChanged())
  }

  def setOwnSelection(sel: Set[String]) = {
    ownSelections = new ListBuffer ++ sel
    backend.writeOwnSelection(ownSelections)
    buildOwnSelection()
    EventBroadcaster.publish(PersonalCostUnitSelectionChanged())
  }

  def newWorkRecord(c: CostUnit, start: Date, duration: Int): WorkRecord = {
    val res = WorkRecord(newWorkRecordId, c, start, duration)
    logger.debug("New WorkRecord " + res)
    save(res)
    res
  }

  def save(r: WorkRecord): WorkRecord = {

    val res: WorkRecord =
      if (r.getId == WorkRecord.NO_ID) {
        logger.warn("DEPRECATED! use newWorkRecord for savong of new work records!")
        WorkRecord(newWorkRecordId, r.costUnit, r.start, r.duration)
      } else {
        // FIXME -= doesnt work. Error in equals/hashCode?
        workRecords = workRecords - r
        r
      }
    logger.debug("Saving work record " + res)
    // FIXME += doesnt work. Error in equals/hashCode?
    workRecords = workRecords + res
    backend.writeWorkRecords(workRecords)
    EventBroadcaster.publish(WorkRecordChangedEvent(r))
    res
  }
  def delete(r: WorkRecord): Unit = {
    workRecords -= r
    backend.writeWorkRecords(workRecords)
    EventBroadcaster.publish(WorkRecordDeletedEvent(r))
  }

  def getWorkRecords(from: Date, to: Date): List[WorkRecord] = {
    logger.warn("Getting work records from %tF %tR to %tF %tR".format(from, from, to, to))
    def within(rec: WorkRecord): Boolean = !rec.getStartTime.before(from) && rec.getStartTime.getTime + rec.getDuration <= to.getTime
    workRecords.filter(r => within(r)).toList.sort(WorkRecord.sortByStartDate)
  }

  def getDurationSummaries(start: Date, end: Date): List[SummaryItem] = {
    val sums = new HashMap[(String, String), Int]()

    def sum(rec: WorkRecord) {
      //      val key = if (groupByAnnotation && rec.getAnnotation != null)
      //        (rec.getAssociatedProject.getId, rec.getAnnotation)
      //      else
      //        (rec.getAssociatedProject.getId, rec.getAssociatedProject.getName)
      val key = (rec.getAssociatedProject.getId, rec.getAssociatedProject.getName)

      sums += (key -> (sums.getOrElse(key, 0) + rec.getDuration))
    }
    getWorkRecords(start, end).foreach(sum(_))

    val res = new ListBuffer[SummaryItem]()

    sums.foreach(entry => res += new SummaryItem(entry._1._1, entry._1._2, entry._2))
    res.toList
  }

}
