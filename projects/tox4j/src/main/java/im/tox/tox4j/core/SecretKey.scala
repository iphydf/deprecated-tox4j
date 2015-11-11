package im.tox.tox4j.core

import im.tox.FixedSizeByteArrayCompanion

final class SecretKey(val value: Array[Byte]) extends AnyVal

object SecretKey extends FixedSizeByteArrayCompanion[SecretKey](ToxCoreConstants.SecretKeySize) {
  def unsafeFromByteArray(value: Array[Byte]): SecretKey = new SecretKey(value)
}
