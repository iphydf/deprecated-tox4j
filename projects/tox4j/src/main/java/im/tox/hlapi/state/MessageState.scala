package im.tox.hlapi.state

import scalaz.Lens

object MessageState {

  final case class MessageList(messages: Map[Int, Message] = Map[Int, Message]())
  final case class Message(messageType: MessageType, timeDelta: Int, content: Array[Byte], messageStatus: MessageStatus)

  sealed abstract class MessageStatus
  final case class MessageSent() extends MessageStatus
  final case class MessageRead() extends MessageStatus
  final case class MessageReceived() extends MessageStatus

  sealed abstract class MessageType
  final case class NormalMessage() extends MessageType
  final case class ActionMessage() extends MessageType

  val messageStatusL = Lens.lensu[Message, MessageStatus](
    (a, value) => a.copy(messageStatus = value),
    _.messageStatus
  )

  val MessageListMessagesL = Lens.lensu[MessageList, Map[Int, Message]](
    (a, value) => a.copy(messages = value),
    _.messages
  )

}
