package im.tox.hlapi

import java.util

import im.tox.hlapi.Event._
import im.tox.tox4j.ToxEventListener
import im.tox.tox4j.av.data._
import im.tox.tox4j.av.enums.ToxavFriendCallState
import im.tox.tox4j.core.data._
import im.tox.tox4j.core.enums._

/**
 * TODO(iphydf): Write comments.
 */
object CollectEventListener extends ToxEventListener[List[Event]] {

  override def selfConnectionStatus(
    connectionStatus: ToxConnection
  )(state: List[Event]): List[Event] = {
    SelfConnectionStatus(connectionStatus) +: state
  }

  override def friendStatus(
    friendNumber: ToxFriendNumber,
    status: ToxUserStatus
  )(state: List[Event]): List[Event] = {
    FriendStatus(friendNumber, status) +: state
  }

  override def friendTyping(
    friendNumber: ToxFriendNumber,
    isTyping: Boolean
  )(state: List[Event]): List[Event] = {
    FriendTyping(friendNumber, isTyping) +: state
  }

  override def friendName(
    friendNumber: ToxFriendNumber,
    name: ToxNickname
  )(state: List[Event]): List[Event] = {
    FriendName(friendNumber, name) +: state
  }

  override def friendMessage(
    friendNumber: ToxFriendNumber,
    messageType: ToxMessageType,
    timeDelta: Int,
    message: ToxFriendMessage
  )(state: List[Event]): List[Event] = {
    FriendMessage(friendNumber, messageType, timeDelta, message) +: state
  }

  override def friendLossyPacket(
    friendNumber: ToxFriendNumber,
    data: ToxLossyPacket
  )(state: List[Event]): List[Event] = {
    FriendLossyPacket(friendNumber, data) +: state
  }

  override def fileRecv(
    friendNumber: ToxFriendNumber,
    fileNumber: Int,
    kind: Int,
    fileSize: Long,
    filename: ToxFilename
  )(state: List[Event]): List[Event] = {
    FileRecv(friendNumber, fileNumber, kind, fileSize, filename) +: state
  }

  override def friendRequest(
    publicKey: ToxPublicKey,
    timeDelta: Int,
    message: ToxFriendRequestMessage
  )(state: List[Event]): List[Event] = {
    FriendRequest(publicKey, timeDelta, message) +: state
  }

  override def fileChunkRequest(
    friendNumber: ToxFriendNumber,
    fileNumber: Int,
    position: Long,
    length: Int
  )(state: List[Event]): List[Event] = {
    FileChunkRequest(friendNumber, fileNumber, position, length) +: state
  }

  override def fileRecvChunk(
    friendNumber: ToxFriendNumber,
    fileNumber: Int,
    position: Long,
    data: Array[Byte]
  )(state: List[Event]): List[Event] = {
    FileRecvChunk(friendNumber, fileNumber, position, data) +: state
  }

  override def friendLosslessPacket(
    friendNumber: ToxFriendNumber,
    data: ToxLosslessPacket
  )(state: List[Event]): List[Event] = {
    FriendLosslessPacket(friendNumber, data) +: state
  }

  override def friendConnectionStatus(
    friendNumber: ToxFriendNumber,
    connectionStatus: ToxConnection
  )(state: List[Event]): List[Event] = {
    FriendConnectionStatus(friendNumber, connectionStatus) +: state
  }

  override def fileRecvControl(
    friendNumber: ToxFriendNumber,
    fileNumber: Int,
    control: ToxFileControl
  )(state: List[Event]): List[Event] = {
    FileRecvControl(friendNumber, fileNumber, control) +: state
  }

  override def friendStatusMessage(
    friendNumber: ToxFriendNumber,
    message: ToxStatusMessage
  )(state: List[Event]): List[Event] = {
    FriendStatusMessage(friendNumber, message) +: state
  }

  override def friendReadReceipt(
    friendNumber: ToxFriendNumber,
    messageId: Int
  )(state: List[Event]): List[Event] = {
    FriendReadReceipt(friendNumber, messageId) +: state
  }

  override def call(
    friendNumber: ToxFriendNumber,
    audioEnabled: Boolean,
    videoEnabled: Boolean
  )(state: List[Event]): List[Event] = {
    Call(friendNumber, audioEnabled, videoEnabled) +: state
  }

  override def callState(
    friendNumber: ToxFriendNumber,
    callState: util.EnumSet[ToxavFriendCallState]
  )(state: List[Event]): List[Event] = {
    CallState(friendNumber, callState) +: state
  }

  override def bitRateStatus(
    friendNumber: ToxFriendNumber,
    audioBitRate: BitRate,
    videoBitRate: BitRate
  )(state: List[Event]): List[Event] = {
    BitRateStatus(friendNumber, audioBitRate, videoBitRate) +: state
  }

  override def audioReceiveFrame(
    friendNumber: ToxFriendNumber,
    pcm: Array[Short],
    channels: AudioChannels,
    samplingRate: SamplingRate
  )(state: List[Event]): List[Event] = {
    AudioReceiveFrame(friendNumber, pcm, channels, samplingRate) +: state
  }

  override def videoReceiveFrame(
    friendNumber: ToxFriendNumber,
    width: Width, height: Height,
    y: Array[Byte], u: Array[Byte], v: Array[Byte],
    yStride: Int, uStride: Int, vStride: Int
  )(state: List[Event]): List[Event] = {
    VideoReceiveFrame(friendNumber, width, height, y, u, v, yStride, uStride, vStride) +: state
  }

}
