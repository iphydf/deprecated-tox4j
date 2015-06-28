package im.tox.tox4j.impl.tcp.messages

import im.tox.tox4j.core.enums.{ ToxConnection, ToxMessageType, ToxUserStatus }
import im.tox.tox4j.impl.tcp.NoSpam

sealed trait Payload

case object BootstrapPayload extends Payload

final case class LosslessPayload(data: Array[Byte]) extends Payload
final case class LossyPayload(data: Array[Byte]) extends Payload
final case class UserStatusPayload(userStatus: ToxUserStatus) extends Payload
final case class ConnectionStatusPayload(connectionStatus: ToxConnection) extends Payload
final case class NamePayload(name: Array[Byte]) extends Payload
final case class StatusMessagePayload(statusMessage: Array[Byte]) extends Payload
final case class TypingPayload(typing: Boolean) extends Payload
final case class MessagePayload(id: Int, messageType: ToxMessageType, timeDelta: Int, message: Array[Byte]) extends Payload
final case class ReadReceiptPayload(id: Int) extends Payload
final case class FriendAddPayload(noSpam: NoSpam, message: Array[Byte]) extends Payload
