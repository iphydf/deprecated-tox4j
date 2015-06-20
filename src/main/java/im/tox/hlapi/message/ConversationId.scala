package im.tox.hlapi.message

import im.tox.hlapi.core._

abstract class ConversationId extends Serializable {
  val key: PublicKey
  val name: String
}
