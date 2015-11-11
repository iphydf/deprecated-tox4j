package im.tox.tox4j.core

import im.tox.FixedSizeByteArrayCompanion

final class PublicKey(val value: Array[Byte]) extends AnyVal

object PublicKey extends FixedSizeByteArrayCompanion[PublicKey](ToxCoreConstants.PublicKeySize) {

  override def unsafeFromByteArray(value: Array[Byte]): PublicKey = new PublicKey(value)

  def fromFriendAddress(expectedFriendAddress: FriendAddress): PublicKey = {
    unsafeFromByteArray(expectedFriendAddress.value.slice(0, Size))
  }

}
