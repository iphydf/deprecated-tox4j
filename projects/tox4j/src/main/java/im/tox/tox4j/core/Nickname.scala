package im.tox.tox4j.core

import im.tox.VariableSizeByteArrayCompanion

final class Nickname(val value: Array[Byte]) extends AnyVal

object Nickname extends VariableSizeByteArrayCompanion[Nickname](ToxCoreConstants.MaxNameLength) {
  override def unsafeFromByteArray(value: Array[Byte]): Nickname = new Nickname(value)
}
