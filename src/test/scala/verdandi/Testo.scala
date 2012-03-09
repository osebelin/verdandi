package verdandi
import verdandi.ui.summary.Period
import verdandi.ui.summary.PeriodType
import java.text.SimpleDateFormat
import java.util.Date

object Testo {

  def main(args: Array[String]) {

    val sdf = new SimpleDateFormat("MMMM yyyy")

    println(sdf.format(new Date()))

  }

}