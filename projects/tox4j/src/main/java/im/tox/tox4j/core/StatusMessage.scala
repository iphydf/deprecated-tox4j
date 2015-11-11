package im.tox.tox4j.core

import im.tox.VariableSizeByteArrayCompanion

final class StatusMessage(val value: Array[Byte]) extends AnyVal

object StatusMessage extends VariableSizeByteArrayCompanion[StatusMessage](ToxCoreConstants.MaxStatusMessageLength) {
  override def unsafeFromByteArray(value: Array[Byte]): StatusMessage = new StatusMessage(value)
}
