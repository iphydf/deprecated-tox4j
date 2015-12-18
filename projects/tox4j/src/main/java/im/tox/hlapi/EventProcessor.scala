package im.tox.hlapi

import java.util

import im.tox.hlapi.Client.ToxClientConnection
import im.tox.tox4j.av.data._
import im.tox.tox4j.av.enums.ToxavFriendCallState
import im.tox.tox4j.core.data._
import im.tox.tox4j.core.enums._

import scalaz.stream.Process

/**
 * TODO(iphydf): Write comments.
 */
trait EventProcessor[S] {

  def selfConnectionStatus(
    connectionStatus: ToxConnection
  )(state: S): Process[ToxClientConnection, S] = Process.emit(state)

  def friendStatus(
    friendNumber: ToxFriendNumber,
    status: ToxUserStatus
  )(state: S): Process[ToxClientConnection, S] = Process.emit(state)

  def friendTyping(
    friendNumber: ToxFriendNumber,
    isTyping: Boolean
  )(state: S): Process[ToxClientConnection, S] = Process.emit(state)

  def friendName(
    friendNumber: ToxFriendNumber,
    name: ToxNickname
  )(state: S): Process[ToxClientConnection, S] = Process.emit(state)

  def friendMessage(
    friendNumber: ToxFriendNumber,
    messageType: ToxMessageType,
    timeDelta: Int,
    message: ToxFriendMessage
  )(state: S): Process[ToxClientConnection, S] = Process.emit(state)

  def friendLossyPacket(
    friendNumber: ToxFriendNumber,
    data: ToxLossyPacket
  )(state: S): Process[ToxClientConnection, S] = Process.emit(state)

  def fileRecv(
    friendNumber: ToxFriendNumber,
    fileNumber: Int,
    kind: Int,
    fileSize: Long,
    filename: ToxFilename
  )(state: S): Process[ToxClientConnection, S] = Process.emit(state)

  def friendRequest(
    publicKey: ToxPublicKey,
    timeDelta: Int,
    message: ToxFriendRequestMessage
  )(state: S): Process[ToxClientConnection, S] = Process.emit(state)

  def fileChunkRequest(
    friendNumber: ToxFriendNumber,
    fileNumber: Int,
    position: Long,
    length: Int
  )(state: S): Process[ToxClientConnection, S] = Process.emit(state)

  def fileRecvChunk(
    friendNumber: ToxFriendNumber,
    fileNumber: Int,
    position: Long,
    data: Array[Byte]
  )(state: S): Process[ToxClientConnection, S] = Process.emit(state)

  def friendLosslessPacket(
    friendNumber: ToxFriendNumber,
    data: ToxLosslessPacket
  )(state: S): Process[ToxClientConnection, S] = Process.emit(state)

  def friendConnectionStatus(
    friendNumber: ToxFriendNumber,
    connectionStatus: ToxConnection
  )(state: S): Process[ToxClientConnection, S] = Process.emit(state)

  def fileRecvControl(
    friendNumber: ToxFriendNumber,
    fileNumber: Int,
    control: ToxFileControl
  )(state: S): Process[ToxClientConnection, S] = Process.emit(state)

  def friendStatusMessage(
    friendNumber: ToxFriendNumber,
    message: ToxStatusMessage
  )(state: S): Process[ToxClientConnection, S] = Process.emit(state)

  def friendReadReceipt(
    friendNumber: ToxFriendNumber,
    messageId: Int
  )(state: S): Process[ToxClientConnection, S] = Process.emit(state)

  def call(
    friendNumber: ToxFriendNumber,
    audioEnabled: Boolean,
    videoEnabled: Boolean
  )(state: S): Process[ToxClientConnection, S] = Process.emit(state)

  def callState(
    friendNumber: ToxFriendNumber,
    callState: util.EnumSet[ToxavFriendCallState]
  )(state: S): Process[ToxClientConnection, S] = Process.emit(state)

  def bitRateStatus(
    friendNumber: ToxFriendNumber,
    audioBitRate: BitRate,
    videoBitRate: BitRate
  )(state: S): Process[ToxClientConnection, S] = Process.emit(state)

  def audioReceiveFrame(
    friendNumber: ToxFriendNumber,
    pcm: Array[Short],
    channels: AudioChannels,
    samplingRate: SamplingRate
  )(state: S): Process[ToxClientConnection, S] = Process.emit(state)

  def videoReceiveFrame(
    friendNumber: ToxFriendNumber,
    width: Width, height: Height,
    y: Array[Byte], u: Array[Byte], v: Array[Byte],
    yStride: Int, uStride: Int, vStride: Int
  )(state: S): Process[ToxClientConnection, S] = Process.emit(state)

}
