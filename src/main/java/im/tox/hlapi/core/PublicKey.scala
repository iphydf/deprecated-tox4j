package im.tox.hlapi.core

import im.tox.tox4j.core.ToxCoreConstants.PUBLIC_KEY_SIZE
import im.tox.hlapi.storage.KeyType

object PublicKey {
  def apply(key: Array[Byte]): Option[PublicKey] = {
    if (key.length == PUBLIC_KEY_SIZE) {
      Some(new PublicKey(key))
    } else {
      None
    }
  }
}

final class PublicKey private (val key: Array[Byte]) extends KeyType
