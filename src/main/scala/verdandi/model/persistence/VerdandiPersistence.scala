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

import verdandi.model.WorkRecord
import verdandi.model.CostUnit
import verdandi.VerdandiConfiguration
import java.util.Date
import scala.swing.Publisher
import verdandi.model.SummaryItem

/**
 * Reading access to CostUnits
 * @author osebelin
 *
 */
trait CostUnitStorage {
  def getActiveCostUnits: List[CostUnit]
}

/**
 * For editing only.
 * @author osebelin
 */
trait CostUnitEditorStorage extends CostUnitStorage {
  def store(c: CostUnit)

  def inactivate(c: CostUnit)

  def activate(c: CostUnit)

  def getCostUnits: List[CostUnit]
}

/** Read-only access to cost units. */
trait CostUnitUserStorage extends CostUnitStorage {
  def getOwnSelection(): List[CostUnit]

  def addToOwnSelection(c: CostUnit)

  def setOwnSelection(sel: Set[String])

}

trait WorkRecordStorage {
  def newWorkRecord(c: CostUnit): WorkRecord = newWorkRecord(c, new Date(), VerdandiConfiguration.timeResolution)
  def newWorkRecord(c: CostUnit, start: Date): WorkRecord = newWorkRecord(c, start, VerdandiConfiguration.timeResolution)
  def newWorkRecord(c: CostUnit, start: Date, duration: Int): WorkRecord
  def save(r: WorkRecord): WorkRecord
  def delete(r: WorkRecord): Unit
  def getWorkRecords(day: Date): List[WorkRecord] = getWorkRecords(day, day)
  /**
   * @return The WorkRecords between from and to, ordered by start date
   */
  def getWorkRecords(from: Date, to: Date): List[WorkRecord]
  def getDurationSummaries(start: Date, end: Date): List[SummaryItem]
}

abstract class AbstractUserStorage extends CostUnitUserStorage with WorkRecordStorage with Publisher {

}
