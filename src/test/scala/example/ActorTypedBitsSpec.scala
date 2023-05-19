package example

import org.apache.pekko.actor.testkit.typed.scaladsl.ActorTestKit
import org.apache.pekko.actor.typed.scaladsl.AskPattern.Askable
import org.apache.pekko.actor.typed.{ActorSystem, Scheduler}
import org.apache.pekko.util.Timeout
import example.Bank._
import org.scalatest.Inside.inside
import org.scalatest.concurrent.ScalaFutures.convertScalaFuture
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}

import scala.concurrent.Future
import scala.concurrent.duration._

class ActorTypedBitsSpec extends AnyFlatSpec
  with BeforeAndAfterAll
  with BeforeAndAfterEach
  with should.Matchers {

  val testKit: ActorTestKit = ActorTestKit()
  implicit val system: ActorSystem[BankOperation] = ActorSystem(Bank("test-back"), "bank-system")
  implicit val timeout: Timeout = 3.seconds
  implicit val scheduler: Scheduler = testKit.scheduler

  override protected def afterAll(): Unit = testKit.shutdownTestKit()

  behavior of "Bank"
  it should "deposit something into bank account" in {

    val probe = testKit.createTestProbe[AccountChangeEvent]()
    val accountRef = testKit.spawn(BankAccountActor("0010001", probe.ref))

    accountRef ! Deposit(100)
    probe.expectMessageType[DepositReceipt]
  }

  it should "withdraw something from bank account" in {
    val probe = testKit.createTestProbe[AccountChangeEvent]()
    val accountRef = testKit.spawn(BankAccountActor("0010001", probe.ref))

    accountRef ! Deposit(100)
    probe.expectMessageType[DepositReceipt]

    accountRef ! Withdraw(50)
    probe.expectMessageType[WithdrawReceipt]
  }

  it should "withdraw something from bank account and get right balance with ask" in {
    val probe = testKit.createTestProbe[AccountChangeEvent]()
    val accountRef = testKit.spawn(BankAccountActor("0010001", probe.ref))

    accountRef ! Deposit(100)
    probe.expectMessageType[DepositReceipt]

    val receipt: Future[AccountOpResponse] = accountRef.ask(ref => Withdraw(25, ref))

    receipt.futureValue should be(WithdrawReceipt("0010001", balance = 75, amount = 25))
  }

  it should "withdraw something from bank account and get with operation" in {
    val probe = testKit.createTestProbe[AccountChangeEvent]()
    val accountRef = testKit.spawn(BankAccountActor("0010001", probe.ref))

    accountRef ! Deposit(100)
    probe.expectMessageType[DepositReceipt]

    accountRef ! Withdraw(amount = 25)
    probe.expectMessageType[WithdrawReceipt]

    val balance: Future[AccountOpResponse] = accountRef.ask(ref => GetBalance(ref))
    inside(balance.futureValue) {
      case GetBalanceReceipt(balance, _) => balance should be(75)
    }
  }

}
