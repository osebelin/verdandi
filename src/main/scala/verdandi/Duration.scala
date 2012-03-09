package verdandi

case class Duration(val minutes: Int) {

  def format(): String = {
    val hrs = minutes / 60;
    val remainingMinutes = minutes % 60;
    "%02d:%02d".format(hrs, remainingMinutes)
  }

}

