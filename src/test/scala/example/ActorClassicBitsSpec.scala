package example

import akka.actor.{ActorSystem, Props}
import akka.pattern.ask
import akka.testkit.{TestKit, TestProbe}
import example.PingPong.{Action, Bounce, PlayerActor}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}

class ActorClassicBitsSpec extends AnyFlatSpec
  with BeforeAndAfterAll
  with BeforeAndAfterEach
  with should.Matchers {

  implicit val system: ActorSystem = ActorSystem("akka-bits")

  override protected def afterAll(): Unit = TestKit.shutdownActorSystem(system)

  behavior of "Player Actor"
  it should "ping and ping once" in {
    val bounce = TestProbe("bounce-actor")
    val playerOneRef = system.actorOf(Props(new PlayerActor("player-1", bounce.ref)))
    val playerTwoRef = system.actorOf(Props(new PlayerActor("player-2", bounce.ref)))

    playerOneRef ! Bounce(playerTwoRef, Action.Ping, count = 4)

    bounce.expectMsg(Bounce(playerTwoRef, Action.Ping, count = 4))
    bounce.expectMsg(Bounce(playerOneRef, Action.Pong, count = 3))
    bounce.expectMsg(Bounce(playerTwoRef, Action.Ping, count = 2))
    bounce.expectMsg(Bounce(playerOneRef, Action.Pong, count = 1))
    bounce.expectNoMessage()
  }

}
