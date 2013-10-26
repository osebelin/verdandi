package verdandi.model

import com.weiglewilczek.slf4s.Logging

class SummaryItem(val projectid: String, val projectName: String, val duration: Int) extends Logging{

  val description = ""

  def formatDurationInManDays(): String = {
    val days = duration / (8 * 60);
    val hrs = duration % (8 * 60) / 60;
    val mins = duration % (8 * 60) % 60;
    "%02d:%02d:%02d".format(days, hrs, mins);
  }
  def formatDurationInManHours() = "%02d:%02d".format(duration / 60, duration % 60);
  def formatDurationInManHoursPercent(overallTime: Int) = {
    val cal1 = (100.0f  / overallTime) : Float;
    val perc = cal1 * duration : Float;
    "%02.02f".format(perc)+"%";
  };
}

object CompareSummaryItemByProjectId extends Ordering[SummaryItem] {
  override def compare(one: SummaryItem, other: SummaryItem): Int = one.projectid.compareTo(other.projectid);
}
object CompareSummaryItemByProjectName extends Ordering[SummaryItem] {
  override def compare(one: SummaryItem, other: SummaryItem): Int = one.projectName.compareTo(other.projectName);
}
object CompareSummaryItemByDuration extends Ordering[SummaryItem] {
  override def compare(one: SummaryItem, other: SummaryItem): Int = one.duration.compareTo(other.duration);
}

