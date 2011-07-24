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
package verdandi.event

import java.util.Date
import scala.swing.Publisher
import scala.reflect.BeanProperty
import scala.swing.event.Event
import verdandi.{ VerdandiException, VerdandiConfiguration }
import verdandi.model.{ CostUnit, WorkRecord }
import com.weiglewilczek.slf4s.Logging

abstract class VerdandiEvent extends Event {
  override def toString(): String = getClass.getSimpleName
}

abstract case class PersistenceEvent() extends VerdandiEvent

case class ConfigurationChangedEvent() extends VerdandiEvent

case class CostUnitEvent(val costUnit: CostUnit) extends PersistenceEvent
case class CostUnitAddedEvent(_c: CostUnit) extends CostUnitEvent(_c)
case class CostUnitRemovedEvent(_c: CostUnit) extends CostUnitEvent(_c)
case class CostUnitChangedEvent(_c: CostUnit) extends CostUnitEvent(_c)
// FIXME: Make this a CostUnitEvent and CostUnitEvent something more special
case class PersonalCostUnitSelectionChanged() extends PersistenceEvent

case class WorkRecordEvent(val workRecord: WorkRecord) extends PersistenceEvent
case class WorkRecordAddedEvent(_wr: WorkRecord) extends WorkRecordEvent(_wr)
case class WorkRecordChangedEvent(_wr: WorkRecord) extends WorkRecordEvent(_wr)
case class WorkRecordDeletedEvent(_wr: WorkRecord) extends WorkRecordEvent(_wr)

case class ErrorEvent(@BeanProperty val cause: VerdandiException, @BeanProperty val message: String) extends VerdandiEvent {
  def this(cause: VerdandiException) = this(cause, null)
}

case class ConfigurationEvent(val config: VerdandiConfiguration) extends VerdandiEvent

/** Issued by the system tray */
case class StartTrackingRequest(@BeanProperty val costunit: CostUnit) extends VerdandiEvent
/** Issued by the system tray */
case class StopTrackingRequest() extends VerdandiEvent

/** Issued by the workdayeditor */
case class StartTrackingWorkRecordEvent(@BeanProperty val workRecord: WorkRecord) extends VerdandiEvent
/** Issued by the workdayeditor */
case class StopTrackingWorkRecordEvent(@BeanProperty val workRecord: WorkRecord) extends VerdandiEvent

/** The application is going to quit. */
case class ApplicationShutdown() extends VerdandiEvent

/**
 * event processing in Verdandi
 */
object EventBroadcaster extends Publisher with Logging {
  private var vListeners: List[PartialFunction[VerdandiEvent, Unit]] = List()

  /**
   * @param evt Gets fired to all listeners.-
   */
  def publish(evt: VerdandiEvent) {
    for (x <- vListeners; if x.isDefinedAt(evt)) {
      logger.warn("applying event " + evt + " to " + x)
      x.apply(evt)
    }
    logger.warn("applying event " + evt + " via super call")

    super.publish(evt);
  }
}
