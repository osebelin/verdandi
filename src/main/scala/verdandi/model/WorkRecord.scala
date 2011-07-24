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

import scala.reflect.BeanProperty
import java.text.DateFormat
import java.util.{ Date, Calendar }
import com.weiglewilczek.slf4s.Logging
/**
 * @param id
 * @param costUnit
 * @param startTime
 * @param dur Duration in minutes!
 */
@serializable
@SerialVersionUID(1)
class WorkRecord(@BeanProperty val id: Int, var costUnit: CostUnit, private var startTime: Date, private var dur: Int) extends Logging {

  @BeanProperty
  var annotation: String = _

  def getAssociatedProject(): CostUnit = costUnit

  def setAssociatedProject(associatedProject: CostUnit) = costUnit = associatedProject

  def start = startTime

  def start_=(_startTime: Date) = setStartTime(_startTime)

  def getStartTime(): Date = startTime
  /**
   * Sets the start time.Time will be adjusted to fit to accuracy.
   * @param _startTime The startTime to set.
   * @return the (possibly adjusted) start time
   */
  def setStartTime(_startTime: Date): Date = {
    startTime = WorkRecord.adjustDate(_startTime)
    startTime
  }

  def end = new Date(startTime.getTime + (dur * 60 * 1000))

  def end_=(endTime: Date) = {
    require(endTime.after(startTime))
    // safe: The duration will never span more than a day
    dur = ((endTime.getTime - startTime.getTime) / (1000 * 60)).toInt
  }

  def duration = dur
  def duration_=(_dur: Int) = dur = _dur
  def label: String = costUnit.getName()
  def getLabel() = label
  def getDescription(): String = costUnit.getDescription()

  override def toString(): String = {
    val dfmt = DateFormat.getDateTimeInstance(DateFormat.SHORT,
      DateFormat.SHORT);
    new StringBuilder()
      .append("WorkRecord (")
      .append(getId()).append("): ")
      .append(getLabel())
      .append(", started ")
      .append(dfmt.format(getStartTime()))
      .append(", duration ")
      .append(getDuration())
      .append(" minutes, ending at ")
      .append(dfmt.format(end)).toString
  }

  def getDuration(): Int = dur

  def setDuration(_duration: Int): Int = {
    dur = WorkRecord.adjustDuration(_duration)
    dur
  }

  def collides(other: WorkRecord): Boolean = {

    var cal = Calendar.getInstance();
    cal.setTime(startTime);
    cal.add(Calendar.MINUTE, dur);
    val myEnd = cal.getTime();

    cal = Calendar.getInstance();
    cal.setTime(other.getStartTime());
    cal.add(Calendar.MINUTE, other.getDuration());
    val otherEnd = cal.getTime();

    var coll = false;

    // Same start time
    coll |= other.getStartTime().equals(startTime);

    // other surrounds our start
    coll |= (other.getStartTime().before(startTime) && otherEnd
      .after(startTime));

    // other surrounds our end
    coll |= (other.getStartTime().before(myEnd) && otherEnd.after(myEnd));

    // we surround others start
    coll |= (startTime.before(other.getStartTime()) && myEnd.after(other
      .getStartTime()));

    // we surround others end
    coll |= (startTime.before(otherEnd) && myEnd.after(otherEnd));
    coll
  }

  /**
   * {@inheritDoc}
   */
  override def hashCode(): Int = id.hashCode

  /**
   * {@inheritDoc}
   */
  override def equals(other: Any): Boolean = other match {
    case that: WorkRecord => this.id == that.id
    case _ => false
  }
}

object WorkRecord extends Logging {
  val NO_ID = -1;

  private def adjust(t: Int): Int = {
    val res = VerdandiConfiguration.timeResolution
    val offset = t % res;
    if (offset > res / 2)
      t + (res - offset)
    else
      t - offset
  }

  def adjustDuration(d: Int): Int = {
    val adjusted = adjust(d)
    if (adjusted == 0)
      VerdandiConfiguration.timeResolution
    else adjusted
  }

  def adjustDate(d: Date): Date = {
    val cal = Calendar.getInstance()
    cal.setTime(d);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);
    val mins = cal.get(Calendar.MINUTE)
    cal.set(Calendar.MINUTE, adjust(mins))
    cal.getTime
  }

  def apply(id: Int, c: CostUnit, start: Date, duration: Int): WorkRecord = {
    new WorkRecord(id, c, adjustDate(start), adjustDuration(duration))
  }
  def apply(id: Int, c: CostUnit, start: Date, duration: Int, _annotation: String): WorkRecord = {
    val res = new WorkRecord(id, c, adjustDate(start), adjustDuration(duration))
    res.annotation = _annotation
    res
  }

  def sortByStartDate(a: WorkRecord, b: WorkRecord): Boolean = {
    a.startTime before b.startTime
  }

  /** Calculates the records offset from the start Hour of a Work day */
  // TODO Maybe this should go into WorkRecordEditor
  def minutesOffset(rec: WorkRecord, startHour: Int): Int = {
    val cal = Calendar.getInstance
    cal.setTime(rec.start)
    cal.set(Calendar.MILLISECOND, 0)
    cal.set(Calendar.SECOND, 0);

    //will crash, if last hr is 23, because it will go until zero 
    logger.trace("Starthour: %d, HOUR_OF_DAY: %d".format(startHour, cal.get(Calendar.HOUR_OF_DAY)))
    require(startHour <= cal.get(Calendar.HOUR_OF_DAY))
    val h =
      if (startHour <= cal.get(Calendar.HOUR_OF_DAY))
        startHour
      else
        cal.get(Calendar.HOUR_OF_DAY)
    (cal.get(Calendar.HOUR_OF_DAY) - h) * 60 + cal.get(Calendar.MINUTE)
  }

}
