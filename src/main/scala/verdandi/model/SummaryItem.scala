package verdandi.model

class SummaryItem(val projectid: String, val projectName: String, val duration: Int) {

  val description = ""

  def formatDurationInManDays(): String = {
    val days = duration / (8 * 60);
    val hrs = duration % (8 * 60) / 60;
    val mins = duration % (8 * 60) % 60;
    "%02d:%02d:%02d".format(days, hrs, mins);
  }
  def formatDurationInManHours() = "%02d:%02d".format(duration / 60, duration % 60);
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

