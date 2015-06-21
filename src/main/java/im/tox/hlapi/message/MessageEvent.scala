package im.tox.hlapi.message

sealed abstract class MessageEvent {
  def id: MessageId
}

final case class NewMessage(val message: Message) extends MessageEvent {
  val id = message.id
}

final case class EditMessage(val message: Message) extends MessageEvent {
  val id = message.id
}

final case class DeleteMessage(val id: MessageId) extends MessageEvent
