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
package verdandi.ui.workdayeditor

import verdandi.ui.VerdandiComponent
import scala.swing._
import verdandi.model.VerdandiConfiguration.WorkDayEditorConfiguration._
import java.awt.{ Color, Dimension, Point, Rectangle }
import java.awt.geom.Line2D
import java.awt.font.TextLayout
import java.util.{ Calendar, Date }
import com.weiglewilczek.slf4s.Logging

/**
 * A canvas with a time schedule.
 */
trait ScheduledCanvas extends VerdandiComponent with Logging {

  minimumSize = new Dimension(400, 600)
  preferredSize = minimumSize

  private[ui] def width = size.width
  private[ui] def height = size.height

  private[ui] val minimumSlotSize = 10

  private[ui] var slotHeight = 10
  private[ui] var slotsPerHour: Int = 4

  // has to be recalculated if CFG changes
  private[ui] def minutesTotal = (lastHour - firstHour) * 60

  private[ui] var pixelsPerMinute: Float = 0F

  // Space between leftMargin and beginning of line
  private[ui] val lineOffset = 50

  private[ui] def topMargin = 10
  private[ui] def bottomMargin = 10
  private[ui] def leftMargin = 5
  private[ui] def rightMargin = 5

  /**
   * Converts a (height) difference in pixels to minutes.
   * Rounds
   *
   * @returns (minutes, remainder)
   */
  private[ui] def pixelsToMinutes(diff: Int): Int = {
    require(pixelsPerMinute > 0)
    logger.trace("%d pixels diff, total mins=%d at height %d, leading to %f pixelPerMin".format(diff, minutesTotal, height, pixelsPerMinute))
    Math.round(diff / pixelsPerMinute)
  }

  private[ui] def minutesToPixels(mins: Int): Int = {
    require(pixelsPerMinute > 0)
    Math.round(mins * pixelsPerMinute)
  }

  def toDate(yPosition: Int): Date = toDate(yPosition, true)

  protected def currentDay: Date

  private def stdCalendar = {
    val cal = Calendar.getInstance()
    cal.setTime(currentDay)
    cal.set(Calendar.HOUR_OF_DAY, firstHour)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)
    cal
  }

  /**
   * @param yPosition
   * @param round If true, round, else cap
   * @return
   */
  def toDate(yPosition: Int, round: Boolean): Date = {
    val cal = stdCalendar

    val today = cal.get(Calendar.DAY_OF_MONTH)

    val minsRaw = ((yPosition - topMargin) / pixelsPerMinute)
    logger.trace("mins raw:" + minsRaw)
    val mins =
      if ((minsRaw % minuteGranularity > minuteGranularity / 2) && round) {
        logger.trace("ROUND!")
        ((minsRaw.toInt / minuteGranularity) * minuteGranularity) + minuteGranularity
      } else {
        logger.trace("CAP")
        (minsRaw.toInt / minuteGranularity) * minuteGranularity
      }
    logger.trace("YPostion %d giving %f raw minutes at %f pixels per minute leading to %d mns " + (if (round) "rounded") + ". Height is %d".format(yPosition, minsRaw, pixelsPerMinute, mins, height))

    cal.add(Calendar.MINUTE, mins.toInt)

    // may roll over so we max it up do the last possible pos.
    if (cal.get(Calendar.DAY_OF_MONTH) > today
      || cal.get(Calendar.HOUR_OF_DAY) >= lastHour) {
      cal.set(Calendar.DAY_OF_MONTH, today)
      cal.set(Calendar.HOUR_OF_DAY, lastHour - 1)
      cal.set(Calendar.MINUTE, 60 - minuteGranularity);
    }

    val res = cal.getTime()
    logger.debug("Resolved y " + yPosition + " to " + res)
    res
  }

  def toPosition(d: Date): Int = {
    val cal = stdCalendar
    val minutes: Int = ((d.getTime - cal.getTime.getTime) / (1000 * 60)).toInt
    (pixelsPerMinute * minutes.toFloat).toInt
  }

  /** Rounds the value to be divisble by minuteGranularity without remainder */
  private def roundToGranularity(minutes: Int): Int = {
    val remainder = minutes % minuteGranularity
    if (remainder > minuteGranularity / 2)
      minutes - remainder
    else
      minutes + minuteGranularity - remainder
  }

  /** Calculates minutes form an y distance  */
  def distanceToMinutes(yFrom: Int, yTo: Int): Int = {
    val minsRaw = ((yTo - yFrom) / pixelsPerMinute).toInt
    roundToGranularity(minsRaw)
  }

  // durch den verdandi cmponent mixin 
  override def setBounds(x: Int, y: Int, w: Int, h: Int) {
    logger.trace("Set bounds!: " + x + "," + y + "@" + w + "x" + h)
    super.setBounds(x, y, w, h)
    recalculateDimensions()

  }

  def recalculateDimensions() {
    val totalHours = lastHour - firstHour
    slotsPerHour = (60 / minuteGranularity)
    slotHeight = ((height - topMargin - bottomMargin).toFloat / (totalHours * slotsPerHour)).toInt
    // calucalte it over the slot height since there may be offsets
    pixelsPerMinute = slotHeight.toFloat / minuteGranularity
    val recal = slotHeight * totalHours * slotsPerHour
    logger.trace("pixelsPerMinute: %f, slotHeight=%d, #slots=%d, slot*slotheight=%d".format(pixelsPerMinute, slotHeight, (totalHours * slotsPerHour), recal))
    minimumSize = new Dimension(minimumSize.width, (totalHours * slotsPerHour * minimumSlotSize));
  }

  override def paintComponent(g: Graphics2D) {

    def drawLines(g: Graphics2D) {
      val g2d: Graphics2D = g.create().asInstanceOf[Graphics2D]
      g2d.setColor(Color.BLACK);

      val x1 = leftMargin + lineOffset
      val x2 = width - rightMargin

      def drawLine(y: Int) {
        g2d.draw(new Line2D.Double(x1, y, x2, y));
      }

      def drawHour(hours: List[Int], y: Int) {
        hours match {
          case List() => ()
          case x :: xs => {
            val hour: String = if (x < 10) "0" + x.toString else x.toString
            val timeLayout = new TextLayout(hour, g2d.getFont(), g2d.getFontRenderContext())
            g2d.drawString(hour, leftMargin, y + (timeLayout.getBounds().getHeight() / 2).toInt);
            // do not draw lines after the last hour.
            if (xs != List())
              for (i <- 0 to slotsPerHour) drawLine(y + i * slotHeight)
            drawHour(xs, y + (slotHeight * slotsPerHour))
          }
        }
      }
      drawHour(List.range(firstHour, lastHour + 1), topMargin)
    }
    super.paintComponent(g) // necessary? Were opaque

    val g2d: Graphics2D = g.create().asInstanceOf[Graphics2D]
    // draw background
    g2d.setColor(Color.WHITE)
    g2d.fillRect(location.x, location.y, width, height)
    drawLines(g2d)
  }

}
