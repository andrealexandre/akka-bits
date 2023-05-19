package example

import org.apache.pekko.actor.{Actor, ActorRef}
import org.apache.pekko.event.LoggingReceive
import com.typesafe.scalalogging.StrictLogging

object PingPong {

  sealed trait Action {
    val nextAction: Action
  }
  object Action {
    case object Ping extends Action {
      override val nextAction: Action = Action.Pong
    }
    case object Pong extends Action {
      override val nextAction: Action = Action.Ping
    }
  }

  case class Bounce(ref: ActorRef, action: Action, count: Int = 0)


  class PlayerActor(val name: String, bounceRef: ActorRef) extends Actor with StrictLogging {

    override def receive: Receive = LoggingReceive {
      case bounce @ Bounce(ref, action, count) if count > 0 =>
        bounceRef ! bounce
        ref ! Bounce(self, action.nextAction, count - 1)
    }

  }

  class BounceActor extends Actor with StrictLogging {
    override def receive: Receive = LoggingReceive {
      case Bounce(ref, action, count) => logger.info(s"Player($ref) => $action => $count")
    }
  }

}
