package im.tox.hlapi.core

import im.tox.hlapi.message.ConversationId
import im.tox.hlapi.storage.ValueType

final case class User(
  val key: PublicKey,
  val name: String
) extends ConversationId with ValueType[PublicKey]
