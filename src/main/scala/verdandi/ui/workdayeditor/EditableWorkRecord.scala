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

import verdandi.event.StopTrackingWorkRecordEvent
import verdandi.event.StartTrackingWorkRecordEvent
import verdandi.event.EventBroadcaster
import verdandi.RichCalendar
import verdandi.ui.Icons
import verdandi.model.VerdandiModel
import verdandi.model.WorkRecord
import verdandi.ui.VerdandiComponent
import scala.swing._
import scala.swing.event._
import java.awt.{ Color, Dimension, Rectangle, Stroke, BasicStroke, Cursor, Point }
import java.awt.geom.Line2D
import java.awt.font.TextLayout
import verdandi.model.VerdandiConfiguration.{ WorkDayEditorConfiguration => CFG }
import java.lang.Math
import java.util.Date
import verdandi.Predef._
import com.weiglewilczek.slf4s.Logging

class EditableWorkRecord(var rec: WorkRecord, editor: WorkDayEditor) extends VerdandiComponent with Logging {

  reactions += {

    case evt: FocusGained => logger.debug("EWR FOCUS GAINED"); repaint
    case evt: FocusLost => logger.debug("EWR FOCUS LOST"); repaint

    case pressed: MousePressed => {
      println("FR(EWR) =>" + requestFocusInWindow())
      // FIXME: Determine button 1
      if ((pressed.modifiers & 1024) == 1024) {
        startDragging(pressed.point)
      }
      logger.trace("Pressesd on EditableWorkRecord:" + pressed.modifiers + " Triggers popup? " + pressed.triggersPopup)
    }
    case evt: MouseDragged => dragged(evt)

    case evt: MouseMoved => {
      if (withinResizeUpThreshold(evt.point))
        editor.cursor = Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR)
      else if (withinResizeDownThreshold(evt.point))
        editor.cursor = Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR)
      else
        editor.cursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR)
    }
    case evt: MouseClicked if evt.clicks >= 2 => {
      requestFocusInWindow()
      logger.debug("FIXME: Handle double click")
      editor.workRecordEditor.edit(rec)
    }
  }

  focusable = true
  listenTo(this.Mouse.clicks)
  listenTo(this.Mouse.moves)

  private[workdayeditor] var tracking = false

  def workRecord = rec

  def textInsets = 2

  private val resizeThreshold = 2

  private var dragStartY: Option[Int] = None

  // Required for movements
  private var dragOffset = 0

  private var dragAction: (MouseDragged => Unit) = null

  /** The y position the dragging started or None */
  def dragStart = dragStartY

  /** Returns true if the mouse position is near the top  */
  def withinResizeUpThreshold(mouse: Point): Boolean = (mouse.y - location.y) <= resizeThreshold

  /** Returns true if the mouse position is near the bottom  */
  def withinResizeDownThreshold(mouse: Point): Boolean = location.y + size.height - mouse.y <= resizeThreshold

  override def toString(): String = "Editing " + rec.toString

  /** The dragging of this record ended after a shift of givenminute count*/
  def draggingEnded() {
    rec = VerdandiModel.workRecordStorage.save(rec);
    dragStartY = None
    dragAction = null
    editor.repaint
  }

  private def startDragging(point: Point) {
    dragStartY = Some(point.y)
    if (withinResizeUpThreshold(point))
      dragAction = resizeUp
    else if (withinResizeDownThreshold(point))
      dragAction = resizeDown
    else {
      dragOffset = point.y - editor.toPosition(rec.start)
      editor.cursor = Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR)
      dragAction = move
    }

  }

  def dragged(evt: MouseDragged) {
    require(dragStartY != None)
    dragAction(evt)
    editor.repaint
    dragStartY = Some(evt.point.y)
  }

  def move(evt: MouseDragged) = {
    val previousStart = rec.start;
    rec.start = editor.toDate(evt.point.y - dragOffset)
    if (!checkRecordBounds) {
      logger.warn("MOVE NOT OK!: " + previousStart + " -> " + rec.start)
      rec.start = previousStart
    } else {
      logger.debug("move OK: " + previousStart + " -> " + rec.start)
    }

  }

  def resizeUp(evt: MouseDragged) = {
    val end = rec.end
    val oldStart = rec.start

    val start = editor.toDate(evt.point.y)
    if (((end.getTime - start.getTime) / (1000 * 60)).intValue >= CFG.minuteGranularity) {
      rec.start = start
      rec.end = end
      if (!checkRecordBounds()) {
        rec.start = oldStart
        rec.end = end
      }

    }
  }

  def resizeDown(evt: MouseDragged) = {
    val oldEnd = rec.end
    rec.end = editor.toDate(evt.point.y)
    if (!checkRecordBounds()) {
      rec.end = oldEnd
    }
  }

  /**
   * Checks, whether the current records bounds are valid, thus do not collide with the other records
   *  and are within the defined start and end hours of the work day editor.
   */
  def checkRecordBounds(): Boolean = {
    def collides(recs: List[EditableWorkRecord]): Boolean = recs match {
      case Nil => true
      case x :: xs => {
        if (x == this) collides(xs)
        else if (x.workRecord.start >= rec.end || x.workRecord.end <= rec.start) collides(xs)
        else false
      }
    }
    def withinBounds(): Boolean = {
      CFG.lowerBound.toDate <= rec.start && CFG.upperBound.toDate >= rec.end
    }
    collides(editor.contents) && withinBounds
  }

  /**
   * Checks, whether the given startDate and duration collide with this record.
   */
  def collides(startDate: Date, duration: Int): Boolean = {
    val endDate = RichCalendar(startDate).addMinute(duration).toDate
    val res = (rec.start <= startDate && rec.end > startDate) || (rec.start < endDate && rec.end >= endDate)
    if (res) {
      logger.debug(startDate + "+" + duration + " collides with " + rec)
    }
    res
  }

  override def paintComponent(g: Graphics2D) {
    logger.trace("Paint EditableWorkRecord " + workRecord.start)
    val g2d: Graphics2D = g.create().asInstanceOf[Graphics2D]

    EditableWorkRecord.switchColor

    val rect = new Rectangle(location.x, location.y, size.width, size.height)
    g2d.setColor(EditableWorkRecord.fillColor)
    g2d.fill(rect)
    g2d.setColor(EditableWorkRecord.borderColor)
    val oldStroke = g2d.getStroke()
    g2d.setStroke(new BasicStroke(2.5F));
    g2d.draw(rect);
    g2d.setStroke(oldStroke);
    if (hasFocus) {
      g2d.setColor(EditableWorkRecord.borderColor.darker)
      val oldStroke = g2d.getStroke()
      g2d.setStroke(new BasicStroke(1.0F));
      g2d.draw(rect);
      g2d.setStroke(oldStroke);
    }

    if (workRecord.label != null) {
      g2d.setColor(Color.BLACK)
      var textLayout = new TextLayout(workRecord.label, g2d.getFont(), g2d.getFontRenderContext())
      while (textLayout.getBounds.getHeight > rect.getBounds.getHeight) {
        val f = g2d.getFont.deriveFont((g2d.getFont.getSize - 1).floatValue)
        g2d.setFont(f);
        textLayout = new TextLayout(workRecord.label, g2d.getFont(), g2d.getFontRenderContext())
      }
      val yOffset: Int =
        if (textLayout.getBounds().getHeight().toInt + textInsets > rect.getBounds.getHeight) {
          textLayout.getBounds().getHeight().toInt
        } else {
          textLayout.getBounds().getHeight().toInt + textInsets
        }
      g2d.drawString(workRecord.label, location.x + textInsets, location.y + yOffset);
    }

    if (tracking) {
      val icon = Icons.getIcon("icon.workdayeditor.currentlytracking")
      val iconx = location.x + size.width - icon.getIconWidth - textInsets
      val icony = location.y + size.height - icon.getIconHeight - textInsets
      icon.paintIcon(this.peer, g2d, iconx, icony);
    }

  }

  def startTracking() {
    logger.debug("Start tracking " + this)
    editor.stopTracking()
    tracking = true
    editor.repaint
    EventBroadcaster.publish(StartTrackingWorkRecordEvent(rec))
  }
  def stopTracking() {
    if (tracking) {
      EventBroadcaster.publish(StopTrackingWorkRecordEvent(rec))
    }
    tracking = false
  }

}

object EditableWorkRecord extends Logging {
  val firstColor = new Color(-8224056)
  val secondColor = new Color(-7237251)

  private var mycolor: Color = null

  def borderColor = mycolor.darker
  def fillColor = mycolor.brighter

  def switchColor: Unit = {
    mycolor = mycolor match {
      case null => firstColor
      case `firstColor` => secondColor
      case `secondColor` => firstColor
    }
    logger.trace("Switch color to " + mycolor)
  }

  def resetColor = mycolor = null

}
