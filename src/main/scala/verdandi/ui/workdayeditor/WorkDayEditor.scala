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
package verdandi.ui.workdayeditor

import verdandi.model.CostUnit
import verdandi.ui.TextResources
import verdandi.ui.VerdandiComponent
import scala.swing._
import scala.swing.event._
import java.util.{ Date, Calendar, Timer, TimerTask }
import java.awt.{ Color, Dimension, Point, Rectangle, Cursor }
import java.awt.geom.Line2D
import java.awt.font.TextLayout
import verdandi.event._
import verdandi.model._
import verdandi.model.VerdandiConfiguration.{ WorkDayEditorConfiguration => CFG }
import verdandi.ui.swing._
import verdandi.{ RichCalendar, RichDate }
import verdandi.Predef._
import com.weiglewilczek.slf4s.Logging

case class BeforePopupEvent(time: Date, recordUnderCursor: Option[EditableWorkRecord]) extends Event

class WorkDayEditor(val model: WorkDayEditorModel = new WorkDayEditorModel) extends VerdandiComponent with Logging with ScheduledCanvas with Container {

  object TrackingUpdater extends TimerTask {
    def update(wr: EditableWorkRecord) {
      if (wr.tracking) {
        val nowAdjusted = WorkRecord.adjustDate(new Date())
        if (nowAdjusted > wr.rec.end) {
          wr.rec.end = nowAdjusted
          storage.save(wr.rec)
          editableRecords.foreach(x => if (x.rec.id == wr.rec.id) x.startTracking())
        }
      }
    }
    def run() = editableRecords.foreach(update(_))
  }

  var workRecordEditor: WorkRecordEditorView = _

  private[workdayeditor] var editableRecords = List[EditableWorkRecord]()

  private val popupMenu = new WorkDayPopup(this)

  private val trackTimer = new Timer

  override def contents = editableRecords

  private def storage = VerdandiModel.workRecordStorage
  private def costUnitStorage = VerdandiModel.costUnitUserStorage

  reactions += {

    case evt: WorkRecordEvent => reload
    case evt: WorkDayChanged => reload

    // TODO: This could replace the other occurrences!
    //case popupTrigger:MouseButtonEvent if popupTrigger.triggersPopup => showPopup(popupTrigger.point)  

    case evt: MousePressed if evt.triggersPopup => requestFocusInWindow(); showPopup(evt.point)
    case evt: MousePressed => requestFocusInWindow(); passOnEvent(evt.point, evt)
    case evt: MouseReleased => {
      cursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR)
      draggingWorkRecord match {
        case Some(wr) => wr.draggingEnded();
        case None if evt.triggersPopup => showPopup(evt.point)
        case None => passOnEvent(evt.point, evt)
      }
    }
    case evt: MouseClicked => {
      recordUnderCursor(evt.point) match {
        case Some(wr) => wr.publish(evt)
        case None => { workRecordEditor.close() }
      }
    }
    case evt: MouseDragged => {
      draggingWorkRecord match {
        case Some(wr) => wr.dragged(evt)
        case None => ()
      }
    }
    case evt: MouseMoved => {
      if (!passOnEvent(evt.point, evt))
        cursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR)
    }

    case evt: StartTrackingRequest => startTracking(evt.costunit)
    case evt: StopTrackingRequest => stopTracking()
    case _: ConfigurationChangedEvent => {
      recalculateDimensions()
      repaint()
    }
  }

  init()

  def init() {
    listenTo(this.Mouse.clicks)
    listenTo(this.Mouse.moves)
    listenTo(model)
    listenTo(EventBroadcaster)
    trackTimer.schedule(TrackingUpdater, 1000, 1000 * 5);
    focusable = true
    reload()
  }

  /** Returns the work record that is currently dragged, if any */
  private def draggingWorkRecord(): Option[EditableWorkRecord] = editableRecords.find(wr => wr.dragStart != None)

  private def recordUnderCursor(pos: Point): Option[EditableWorkRecord] = {
    def isUnder(wr: EditableWorkRecord): Boolean = {
      val rect = wr.bounds
      rect.x <= pos.x && rect.x + rect.width >= pos.x && rect.y <= pos.y && rect.y + rect.height >= pos.y
    }
    editableRecords.find(isUnder(_))
  }

  private def showPopup(point: Point) {
    publish(BeforePopupEvent(toDate(point.y, false), recordUnderCursor(point)))
    popupMenu.show(this, point.x, point.y)
  }

  private def passOnEvent(point: Point, evt: Event): Boolean = {
    recordUnderCursor(point) match {
      case Some(wr) => wr.publish(evt); true
      case None => false
    }
  }

  def reload() = {
    logger.debug("reload (and repaint)")
    load()
    repaint()
  }

  /** Reloads the work records */
  def load() {
    val wr = model.getWorkRecords
    // the optional currently tracking work record
    val trackingRecord = editableRecords.find(_.tracking)
    editableRecords = wr.map(new EditableWorkRecord(_, this))

    // the optional currently tracking work record gets restored 
    trackingRecord.foreach(cur => editableRecords.foreach(r => if (r.rec.id == cur.rec.id) r.tracking = true))

    setBoundsOnContents()
    logger.trace("today has " + editableRecords.size + " work records: " + editableRecords)
  }

  override def minimumSize: Dimension = new Dimension(300, CFG.minHeight)

  override def paintComponent(g: Graphics2D) {
    logger.trace("Paint component: Location: " + location + " / " + locationOnScreen + " and forwarding to " + editableRecords.size + " work records")
    super.paintComponent(g)
    // may be neccesary on dragging
    setBoundsOnContents
    EditableWorkRecord.resetColor
    editableRecords.foreach(_.paintComponent(g))

  }

  /**
   * Calculates the coordinates of a work records
   * @return (yStart, height)
   */
  private def yCoordinates(wr: WorkRecord): (Int, Int) = {
    val totalSlots = (CFG.lastHour - CFG.firstHour) * slotsPerHour
    val slotOffset = WorkRecord.minutesOffset(wr, CFG.firstHour) / CFG.minuteGranularity
    val yStart = slotOffset * slotHeight
    val durAsHeight = wr.duration / CFG.minuteGranularity * slotHeight
    (yStart, durAsHeight)
  }

  private def setBoundsOnContents() {
    val x1 = leftMargin + lineOffset
    val x2 = width - rightMargin
    val contentWidth = (x2 - x1)
    def setBoundsOnContents(xxs: List[EditableWorkRecord]): Unit = xxs match {
      case List() => ()
      case wr :: xs => {
        val (yStart, recHeight) = yCoordinates(wr.workRecord)
        wr.setBounds(x1, yStart + topMargin, contentWidth, recHeight)
        setBoundsOnContents(xs)
      }
    }
    setBoundsOnContents(contents)
  }

  // durch den verdandi component mixin 
  override def setBounds(x: Int, y: Int, w: Int, h: Int) {
    super.setBounds(x, y, w, h)
    setBoundsOnContents()
  }

  def stopTracking() = {
    editableRecords.foreach(_.stopTracking())
    repaint()
  }

  def startTracking(costUnit: CostUnit) {

    def collides(startDate: Date, duration: Int): Boolean = {
      editableRecords.exists(r => r.collides(startDate, duration)) || RichCalendar(startDate).addMinute(duration).toDate() >= CFG.upperBound.toDate
    }
    def nextPossibleStartDate(cal: RichCalendar, possibleOffsets: List[Int]): Option[RichDate] = possibleOffsets match {
      case List() => None
      case x :: xs => {
        logger.debug("Checking next possible start date " + cal.getTime + ", remaining offsets:" + possibleOffsets)
        if (collides(cal.getTime, CFG.minuteGranularity)) nextPossibleStartDate(cal.addMinute(x), xs)
        else Some(cal.getTime)
      }
    }
    // FIXME: Ugly code!
    val cand = nextPossibleStartDate(RichCalendar(WorkRecord.adjustDate(new Date())), List(0, 1, 1).map(_ * CFG.minuteGranularity))

    if (cand.isDefined) {
      val newRecord = VerdandiModel.workRecordStorage.newWorkRecord(costUnit, cand.get)
      // meanwhile in a distant event broadacster.. 
      editableRecords.find(_.rec.id == newRecord.id) match {
        case Some(wr) => wr.startTracking()
        case None => { logger.warn("Did not find record to track") }
      }
    } else {
      logger.warn("No free place to start tracking")
    }

  }

}

