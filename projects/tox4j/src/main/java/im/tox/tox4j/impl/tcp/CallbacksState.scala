package im.tox.tox4j.impl.tcp

import im.tox.tox4j.core.callbacks._

final case class CallbacksState(
  selfConnectionStatusCallback: SelfConnectionStatusCallback = SelfConnectionStatusCallback.IGNORE,
  friendNameCallback: FriendNameCallback = FriendNameCallback.IGNORE,
  friendStatusMessageCallback: FriendStatusMessageCallback = FriendStatusMessageCallback.IGNORE,
  friendStatusCallback: FriendStatusCallback = FriendStatusCallback.IGNORE,
  friendConnectionStatusCallback: FriendConnectionStatusCallback = FriendConnectionStatusCallback.IGNORE,
  friendTypingCallback: FriendTypingCallback = FriendTypingCallback.IGNORE,
  readReceiptCallback: FriendReadReceiptCallback = FriendReadReceiptCallback.IGNORE,
  friendRequestCallback: FriendRequestCallback = FriendRequestCallback.IGNORE,
  friendMessageCallback: FriendMessageCallback = FriendMessageCallback.IGNORE,
  fileControlCallback: FileRecvControlCallback = FileRecvControlCallback.IGNORE,
  fileRequestChunkCallback: FileChunkRequestCallback = FileChunkRequestCallback.IGNORE,
  fileRecvCallback: FileRecvCallback = FileRecvCallback.IGNORE,
  fileRecvChunkCallback: FileRecvChunkCallback = FileRecvChunkCallback.IGNORE,
  friendLossyPacketCallback: FriendLossyPacketCallback = FriendLossyPacketCallback.IGNORE,
  friendLosslessPacketCallback: FriendLosslessPacketCallback = FriendLosslessPacketCallback.IGNORE
)
