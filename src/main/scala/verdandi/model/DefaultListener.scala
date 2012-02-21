package verdandi.model
import scala.swing.Reactor
import verdandi.event.EventBroadcaster

trait DefaultListener extends Reactor {
  listenTo(EventBroadcaster)
}