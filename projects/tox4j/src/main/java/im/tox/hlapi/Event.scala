package im.tox.hlapi

import java.util

import im.tox.tox4j.av.data._
import im.tox.tox4j.av.enums.ToxavFriendCallState
import im.tox.tox4j.core.data._
import im.tox.tox4j.core.enums.{ToxFileControl, ToxMessageType, ToxUserStatus, ToxConnection}

sealed trait Event
object Event {

  final case class FriendTyping(friendNumber: ToxFriendNumber, isTyping: Boolean) extends Event

  final case class FriendStatusMessage(friendNumber: ToxFriendNumber, message: ToxStatusMessage) extends Event

  final case class FileChunkRequest(friendNumber: ToxFriendNumber, fileNumber: Int, position: Long, length: Int) extends Event

  final case class FileRecvChunk(friendNumber: ToxFriendNumber, fileNumber: Int, position: Long, data: Array[Byte]) extends Event

  final case class FriendConnectionStatus(friendNumber: ToxFriendNumber, connectionStatus: ToxConnection) extends Event

  final case class FriendRequest(publicKey: ToxPublicKey, timeDelta: Int, message: ToxFriendRequestMessage) extends Event

  final case class FriendLossyPacket(friendNumber: ToxFriendNumber, data: ToxLossyPacket) extends Event

  final case class FriendStatus(friendNumber: ToxFriendNumber, status: ToxUserStatus) extends Event

  final case class SelfConnectionStatus(connectionStatus: ToxConnection) extends Event

  final case class FriendReadReceipt(friendNumber: ToxFriendNumber, messageId: Int) extends Event

  final case class FriendName(friendNumber: ToxFriendNumber, name: ToxNickname) extends Event

  final case class FriendLosslessPacket(friendNumber: ToxFriendNumber, data: ToxLosslessPacket) extends Event

  final case class FriendMessage(friendNumber: ToxFriendNumber, messageType: ToxMessageType, timeDelta: Int, message: ToxFriendMessage) extends Event

  final case class FileRecv(friendNumber: ToxFriendNumber, fileNumber: Int, kind: Int, fileSize: Long, filename: ToxFilename) extends Event

  final case class FileRecvControl(friendNumber: ToxFriendNumber, fileNumber: Int, control: ToxFileControl) extends Event

  final case class AudioReceiveFrame(friendNumber: ToxFriendNumber, pcm: Array[Short], channels: AudioChannels, samplingRate: SamplingRate) extends Event

  final case class BitRateStatus(friendNumber: ToxFriendNumber, audioBitRate: BitRate, videoBitRate: BitRate) extends Event

  final case class Call(friendNumber: ToxFriendNumber, audioEnabled: Boolean, videoEnabled: Boolean) extends Event

  final case class CallState(friendNumber: ToxFriendNumber, callState: util.EnumSet[ToxavFriendCallState]) extends Event

  final case class VideoReceiveFrame(friendNumber: ToxFriendNumber, width: Width, height: Height, y: Array[Byte], u: Array[Byte], v: Array[Byte], yStride: Int, uStride: Int, vStride: Int) extends Event

}
