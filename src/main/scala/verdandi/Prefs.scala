package verdandi
import java.util.prefs.Preferences
import scala.swing.Reactor
import event.EventBroadcaster
import verdandi.event.ApplicationShutdown

trait Prefs extends Reactor {

  val prefStore = Preferences.userNodeForPackage(getClass())

  val prefs: Map[String, Pref]

  listenTo(EventBroadcaster)

  def load() {
    prefs.foreach(entry => entry._2.read(entry._1, prefStore))
  }

  reactions += {
    case evt: ApplicationShutdown => {
      storePrefs()
      prefs.foreach(entry => entry._2.put(entry._1, prefStore))
    }
  }

  def storePrefs()

}

abstract case class Pref() {
  def put(key: String, to: Preferences)
  def read(key: String, from: Preferences)
}

case class IntPref(var value: Int) extends Pref {
  override def put(key: String, to: Preferences) {
    to.putInt(key, value);
  }
  override def read(key: String, from: Preferences) {
    value = from.getInt(key, value);
  }
}
case class StringPref(var value: String) extends Pref {
  override def put(key: String, to: Preferences) {
    to.put(key, value);
  }
  override def read(key: String, from: Preferences) {
    value = from.get(key, value);
  }
}
