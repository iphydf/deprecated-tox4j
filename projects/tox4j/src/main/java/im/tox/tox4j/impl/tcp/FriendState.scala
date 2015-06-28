package im.tox.tox4j.impl.tcp

import im.tox.tox4j.core.enums.{ ToxConnection, ToxUserStatus }

final case class FriendState(
  publicKey: PublicKey,
  connectionStatus: ToxConnection,
  userStatus: ToxUserStatus,
  name: Array[Byte],
  statusMessage: Array[Byte],
  typing: Boolean,
  /**
   * The next free message id.
   */
  messageId: Int
)
