package verdandi

object Testo {

  def main(args: Array[String]) {
    val dFrom = RichCalendar()
    println(dFrom)

    dFrom.add(2).to().dayOfWeek();
    println(dFrom)

    dFrom.add(1).to().month()
    println(dFrom)
    dFrom.zeroAll.below().dayOfWeek();
    println(dFrom)
    dFrom.zeroAll.below().month();
    println(dFrom)
  }

}