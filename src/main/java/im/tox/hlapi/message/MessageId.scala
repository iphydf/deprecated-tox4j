package im.tox.hlapi.message

import im.tox.hlapi.storage.KeyType

final case class MessageId private[hlapi] (val id: Int) extends KeyType
