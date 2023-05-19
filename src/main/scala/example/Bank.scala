package example

import org.apache.pekko.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}
import org.apache.pekko.actor.typed.{ActorRef, Behavior}

import java.time.ZonedDateTime

object Bank {

  type AccountNumber = String

  sealed trait BankOperation
  case object Start extends BankOperation
  case class CreateAccount(accountNumber: AccountNumber, balance: BigDecimal, replyTo: ActorRef[ActorRef[AccountOperation]] = null) extends BankOperation
  case class GetAccount(accountNumber: AccountNumber, replyTo: ActorRef[ActorRef[AccountOperation]]) extends BankOperation

  sealed trait AccountOperation
  case class Deposit(amount: BigDecimal, sender: ActorRef[AccountOpResponse] = null) extends AccountOperation
  case class Withdraw(amount: BigDecimal, sender: ActorRef[AccountOpResponse] = null) extends AccountOperation
  case class GetBalance(sender: ActorRef[AccountOpResponse]) extends AccountOperation

  sealed trait AccountOpResponse
  case class GetBalanceReceipt(balance: BigDecimal, timestamp: ZonedDateTime) extends AccountOpResponse

  sealed trait AccountChangeEvent
  case class DepositReceipt(accountNumber: AccountNumber, balance: BigDecimal, amount: BigDecimal)
    extends AccountChangeEvent with AccountOpResponse
  case class WithdrawReceipt(accountNumber: AccountNumber, balance: BigDecimal, amount: BigDecimal)
    extends AccountChangeEvent with AccountOpResponse

  case class Transaction(amount: BigDecimal, balance: BigDecimal)

  def apply(name: String): Behavior[BankOperation] = Behaviors.setup { context =>
    context.log.info(s"Creating Bank [$name]...")

    val transactionsRef = context.spawn(BankTransactionActor(), "bank-transactions")
    var accounts: Map[AccountNumber, ActorRef[AccountOperation]] =
      Seq("00100001")
      .map(number => number -> context.spawn(BankAccountActor(number, transactionsRef), s"bank-account-$number"))
      .toMap

    Behaviors.receiveMessage {
      case Start =>
        context.log.info(s"Started Bank [$name]")
        Behaviors.same
      case CreateAccount(number, balance, replyTo) =>
        accounts = accounts.updatedWith(number) {
          case account @ Some(_) =>
            context.log.warn(s"Account $number already exists")
            account
          case None =>
            val accountRef = context.spawn(BankAccountActor(number, transactionsRef, balance), s"bank-account-$number")
            Option(replyTo).foreach(_ ! accountRef)
            Some(accountRef)
        }
        Behaviors.same
      case GetAccount(number, sender) =>
        accounts.get(number) match {
          case Some(accountRef) => sender ! accountRef
          case None => context.log.warn(s"Account $number not found")
        }
        Behaviors.same
    }
  }

  object BankAccountActor {
    def apply(number: AccountNumber,
              transactionActor: ActorRef[AccountChangeEvent],
              balance: BigDecimal = 0): Behavior[AccountOperation] = Behaviors.setup { context =>
      new BankAccountActor(context, number, transactionActor, balance)
    }
  }
  class BankAccountActor(context: ActorContext[AccountOperation],
                         number: AccountNumber,
                         transactionActor: ActorRef[AccountChangeEvent],
                         initBalance: BigDecimal = 0)
    extends AbstractBehavior[AccountOperation](context) {

    var balance: BigDecimal = initBalance

    override def onMessage(msg: AccountOperation): Behavior[AccountOperation] = {
      msg match {
        case Deposit(amount, sender) =>
          balance += amount
          val receipt = DepositReceipt(number, balance, amount)
          transactionActor ! receipt
          Option(sender).foreach(_ ! receipt)

        case Withdraw(amount, sender) =>
          balance -= amount
          val receipt = WithdrawReceipt(number, balance, amount)
          transactionActor ! receipt
          Option(sender).foreach(_ ! receipt)

        case GetBalance(sender) =>
          sender ! GetBalanceReceipt(balance, ZonedDateTime.now())
      }
      Behaviors.same
    }
  }

  object BankTransactionActor {
    def apply(): Behavior[AccountChangeEvent] = Behaviors.setup { context =>
      new BankTransactionActor(context)
    }
  }
  class BankTransactionActor(context: ActorContext[AccountChangeEvent])
    extends AbstractBehavior[AccountChangeEvent](context) {
    private var transactions = Map[AccountNumber, List[Transaction]]()

    override def onMessage(msg: AccountChangeEvent): Behavior[AccountChangeEvent] = {
      msg match {
        case DepositReceipt(accountNumber, balance, amount) =>
          transactions = transactions.updatedWith(accountNumber) {
            case Some(transactions) => Some(Transaction(amount, balance) +: transactions)
            case None => Some(List(Transaction(amount, balance)))
          }

        case WithdrawReceipt(accountNumber, balance, amount) =>
          transactions = transactions.updatedWith(accountNumber) {
            case Some(transactions) => Some(Transaction(amount, -balance) +: transactions)
            case None => Some(List(Transaction(amount, balance)))
          }
      }
      Behaviors.same
    }
  }

}