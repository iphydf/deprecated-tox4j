package im.tox.hlapi.storage

import scala.collection.immutable.{ HashMap, Map }

import org.scalacheck._
import org.scalacheck.commands._
import org.scalacheck.Arbitrary._

import org.scalatest._
import org.scalatest.prop.PropertyChecks

import im.tox.hlapi.message.{ ConversationId, Message, MessageId }

final case class LogSpecification(
    newSut: Iterable[(ConversationId, Message)] => LogStorage
) extends Commands {

  type Sut = LogStorage

  final case class State(val log: HashMap[ConversationId, List[Message]]) {
    def lookup(conversation: ConversationId): List[Message] =
      log.get(conversation).getOrElse(List())
    def append(conversation: ConversationId, msg: Message): State =
      copy(log = log + ((conversation, lookup(conversation) :+ msg)))
    def delete(conversation: ConversationId): State =
      copy(log = log - conversation)
    def delete(conversation: ConversationId, message: MessageId): State = {
      val conv = lookup(conversation).filter(_.id != message)
      copy(log = log + ((conversation, conv)))
    }
    def modify(conversation: ConversationId, message: Message): State = {
      val conv = lookup(conversation)
      conv.indexWhere(_.id == message.id) match {
        case -1 => this

        case index: Int =>
          copy(log = log + ((conversation, conv.updated(index, message))))
      }
    }
  }

  def newSut(s: State): Sut =
    newSut(s.log.flatMap { case ((id: ConversationId, log: List[Message])) => log.map((id, _)) })

  def genInitialState: Gen[State] = {
    for {
      ids <- Gen.nonEmptyContainerOf[List, ConversationId](genConv)
      logs <- Gen.containerOfN[List, List[Message]](
        ids.size,
        Gen.nonEmptyContainerOf[List, Message](genMsg)
      )
    } yield State(HashMap(ids.zip(logs): _*))
  }

  def initialPreCondition(s: State): Boolean = true
  def canCreateNewSut(newState: State, initStuts: Traversable[State],
    runningStuts: Traversable[Sut]): Boolean = true

  def destroySut(sut: Sut): Unit = ()

  final case class Lookup(conversation: ConversationId) extends SuccessCommand {
    type Result = Iterable[Message]
    def nextState(s: State): State = s

    def preCondition(s: State): Boolean = true
    def postCondition(s: State, r: Result): Prop = {
      val log = s.lookup(conversation)
      Prop(
        r.size == log.size
          && r.zip(log).forall { case (a, b) => a == b }
      )
    }

    def run(sut: Sut): Result = {
      sut.lookup(conversation)
    }
  }

  final case class Append(
      conversation: ConversationId,
      message: Message
  ) extends SuccessCommand {
    type Result = Unit
    def nextState(s: State): State = {
      s.append(conversation, message)
    }

    def preCondition(s: State): Boolean = true
    def postCondition(s: State, r: Result): Prop = Prop(true)

    def run(sut: Sut): Result = {
      sut.append(conversation, message)
    }

  }

  final case class Modify(
      conversation: ConversationId,
      message: Message
  ) extends SuccessCommand {
    type Result = Unit
    def nextState(s: State): State = {
      s.modify(conversation, message)
    }

    def postCondition(s: State, r: Result): Prop = Prop(true)
    def preCondition(s: State): Boolean = {
      s.lookup(conversation).exists(_.id == message.id)
    }

    def run(sut: Sut): Result = {
      sut.modify(conversation, message)
    }

  }

  final case class DeleteConv(conversation: ConversationId)
      extends SuccessCommand {
    type Result = Boolean
    def nextState(s: State): State = s.delete(conversation)

    def preCondition(s: State): Boolean = true
    def postCondition(s: State, r: Result): Prop = Prop(r)

    def run(sut: Sut): Result = {
      sut.delete(conversation)
      sut.lookup(conversation).isEmpty
    }

  }

  final case class DeleteMsg(conversation: ConversationId, message: MessageId)
      extends SuccessCommand {
    type Result = Unit
    def nextState(s: State): State = s.delete(conversation, message)

    def preCondition(s: State): Boolean = true
    def postCondition(s: State, r: Result): Prop = Prop(true)

    def run(sut: Sut): Result = {
      sut.delete(conversation, message)
    }

  }

  // TODO(nbraud) write generators
  def genConv: Gen[ConversationId] = ??? // scalastyle:ignore not.implemented.error.usage
  def genMsg: Gen[Message] = ???         // scalastyle:ignore not.implemented.error.usage
  def genMsgId: Gen[MessageId] = ???     // scalastyle:ignore not.implemented.error.usage

  def genLookup(s: State): Gen[Lookup] = {
    genConv.map(Lookup(_))
  }

  def genDeleteConv(s: State): Gen[DeleteConv] = {
    genConv.map(DeleteConv(_))
  }

  def genDeleteMsg(s: State): Gen[DeleteMsg] = {
    for {
      conversation <- genConv;
      messageId <- genMsgId
    } yield DeleteMsg(conversation, messageId)
  }

  def genAppend(s: State): Gen[Append] = {
    for {
      conversation <- genConv;
      message <- genMsg
    } yield Append(conversation, message)
  }

  def genModify(s: State): Gen[Modify] = {
    for {
      conversation <- genConv;
      message <- genMsg
    } yield Modify(conversation, message)
  }

  def genCommand(s: State): Gen[Command] = {
    Gen.oneOf[Command](
      genLookup(s), genDeleteConv(s),
      genDeleteMsg(s), genAppend(s),
      genModify(s)
    )
  }
}
