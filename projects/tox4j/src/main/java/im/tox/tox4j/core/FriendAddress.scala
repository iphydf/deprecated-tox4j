package im.tox.tox4j.core

import im.tox.FixedSizeByteArrayCompanion

final class FriendAddress(val value: Array[Byte]) extends AnyVal

object FriendAddress extends FixedSizeByteArrayCompanion[FriendAddress](ToxCoreConstants.AddressSize) {
  override def unsafeFromByteArray(value: Array[Byte]): FriendAddress = new FriendAddress(value)
}
