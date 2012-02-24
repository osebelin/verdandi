package verdandi
import org.junit.runner.RunWith
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.Spec
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class RichCalendarSpec extends Spec with ShouldMatchers {

  describe("A richDate") {
    it("") {
      val dFrom = RichCalendar()
      println(dFrom)

      dFrom.add(2).to().dayOfWeek();
      println(dFrom)

    }
  }

}