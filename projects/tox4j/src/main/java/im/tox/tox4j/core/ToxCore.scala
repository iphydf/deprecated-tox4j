package im.tox.tox4j.core

import im.tox.core.network.Port
import im.tox.tox4j.core.data._
import im.tox.tox4j.core.enums.{ToxFileControl, ToxMessageType}
import im.tox.tox4j.core.exceptions._
import im.tox.tox4j.core.options.ToxOptions
import org.jetbrains.annotations.NotNull

/**
 * This class overrides the methods in [[ToxCoreT]] that can fail, so Java doesn't see [[Object]] everywhere.
 */
trait ToxCore[ToxCoreState] extends ToxCoreT[ToxCoreState, IdentityResult, ToxCore[ToxCoreState]] {

  @throws[ToxNewException]
  override def load(@NotNull options: ToxOptions): ToxCore[ToxCoreState]
  @throws[ToxFriendGetPublicKeyException]
  override def getFriendPublicKey(friendNumber: Int): ToxPublicKey
  @throws[ToxFriendAddException]
  override def addFriend(@NotNull address: ToxFriendAddress, @NotNull message: ToxFriendRequestMessage): Int
  @throws[ToxFriendAddException]
  override def addFriendNorequest(@NotNull publicKey: ToxPublicKey): Int
  @throws[ToxSetTypingException]
  override def setTyping(friendNumber: Int, typing: Boolean): Unit
  @throws[ToxFileGetException]
  override def getFileFileId(friendNumber: Int, fileNumber: Int): ToxFileId
  @throws[ToxFriendSendMessageException]
  override def friendSendMessage(friendNumber: Int, @NotNull messageType: ToxMessageType, timeDelta: Int, @NotNull message: ToxFriendMessage): Int
  @throws[ToxFriendCustomPacketException]
  override def friendSendLossyPacket(friendNumber: Int, @NotNull data: ToxLossyPacket): Unit
  @throws[ToxBootstrapException]
  override def bootstrap(@NotNull address: String, port: Port, @NotNull publicKey: ToxPublicKey): Unit
  @throws[ToxFileControlException]
  override def fileControl(friendNumber: Int, fileNumber: Int, @NotNull control: ToxFileControl): Unit
  @throws[ToxFriendCustomPacketException]
  override def friendSendLosslessPacket(friendNumber: Int, @NotNull data: ToxLosslessPacket): Unit
  @throws[ToxSetInfoException]
  override def setName(@NotNull name: ToxNickname): Unit
  @throws[ToxFileSendChunkException]
  override def fileSendChunk(friendNumber: Int, fileNumber: Int, position: Long, @NotNull data: Array[Byte]): Unit
  @throws[ToxFileSeekException]
  override def fileSeek(friendNumber: Int, fileNumber: Int, position: Long): Unit
  @throws[ToxFriendByPublicKeyException]
  override def friendByPublicKey(@NotNull publicKey: ToxPublicKey): Int
  @throws[ToxFriendDeleteException]
  override def deleteFriend(friendNumber: Int): Unit
  @throws[ToxFileSendException]
  override def fileSend(friendNumber: Int, kind: Int, fileSize: Long, @NotNull fileId: ToxFileId, @NotNull filename: ToxFilename): Int
  @throws[ToxSetInfoException]
  override def setStatusMessage(@NotNull message: ToxStatusMessage): Unit
  @throws[ToxGetPortException]
  override def getUdpPort: Port
  @throws[ToxGetPortException]
  override def getTcpPort: Port
  @throws[ToxBootstrapException]
  override def addTcpRelay(@NotNull address: String, port: Port, @NotNull publicKey: ToxPublicKey): Unit

}
