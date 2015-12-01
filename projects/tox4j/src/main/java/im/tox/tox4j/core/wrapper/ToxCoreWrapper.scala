package im.tox.tox4j.core.wrapper

import im.tox.core.network.Port
import im.tox.tox4j.core._
import im.tox.tox4j.core.callbacks.ToxEventListener
import im.tox.tox4j.core.data._
import im.tox.tox4j.core.enums.{ToxFileControl, ToxMessageType, ToxUserStatus}
import im.tox.tox4j.core.exceptions._
import im.tox.tox4j.exceptions.ToxException

import scala.language.higherKinds
import scala.reflect.ClassTag

/**
 * Wrapper for an exception-throwing [[ToxCore]] instance that catches all
 * exceptions and returns the error code or the result in an either-type
 * defined by the subclass. See [[ToxCoreDisjunction]] and [[ToxCoreEither]].
 *
 * No methods of this class can throw an exception.
 *
 * TODO(iphydf): Find a better name for this.
 */
// scalastyle:off line.size.limit
abstract class ToxCoreWrapper[ToxCoreState, Result[+E <: Enum[E], +_], Self <: ToxCoreWrapper[ToxCoreState, Result, Self]](tox: ToxCore[ToxCoreState])
    extends ToxCoreT[ToxCoreState, Result, Self] {

  override def getSavedata: Array[Byte] = tox.getSavedata
  override def getFriendList: Array[Int] = tox.getFriendList
  override def setStatus(status: ToxUserStatus): Unit = tox.setStatus(status)
  override def friendExists(friendNumber: Int): Boolean = tox.friendExists(friendNumber)
  override def iterationInterval: Int = tox.iterationInterval
  override def getName: ToxNickname = tox.getName
  override def callback(handler: ToxEventListener[ToxCoreState]): Unit = tox.callback(handler)
  override def getSecretKey: ToxSecretKey = tox.getSecretKey
  override def setNospam(nospam: Int): Unit = tox.setNospam(nospam)
  override def getDhtId: ToxPublicKey = tox.getDhtId
  override def getStatus: ToxUserStatus = tox.getStatus
  override def getPublicKey: ToxPublicKey = tox.getPublicKey
  override def getStatusMessage: ToxStatusMessage = tox.getStatusMessage
  override def iterate(state: ToxCoreState): ToxCoreState = tox.iterate(state)
  override def getNospam: Int = tox.getNospam
  override def close(): Unit = tox.close()
  override def getAddress: ToxFriendAddress = tox.getAddress

  protected def toLeft[E <: Enum[E]](e: E): Result[E, Nothing]
  protected def toRight[A](a: A): Result[Nothing, A]

  @SuppressWarnings(Array("org.brianmckenna.wartremover.warts.Any"))
  protected final def catchErrorCode[A, E <: Enum[E], Exn <: ToxException[E]](
    block: => A
  )(implicit evidence: ClassTag[Exn]): Result[E, A] = {
    try {
      toRight(block)
    } catch {
      case e: Exn => toLeft(e.code)
    }
  }

  override def getFriendPublicKey(friendNumber: Int): Result[ToxFriendGetPublicKeyException.Code, ToxPublicKey] = {
    catchErrorCode[ToxPublicKey, ToxFriendGetPublicKeyException.Code, ToxFriendGetPublicKeyException](tox.getFriendPublicKey(friendNumber))
  }

  override def addFriend(address: ToxFriendAddress, message: ToxFriendRequestMessage): Result[ToxFriendAddException.Code, Int] = {
    catchErrorCode[Int, ToxFriendAddException.Code, ToxFriendAddException](tox.addFriend(address, message))
  }

  override def addFriendNorequest(publicKey: ToxPublicKey): Result[ToxFriendAddException.Code, Int] = {
    catchErrorCode[Int, ToxFriendAddException.Code, ToxFriendAddException](tox.addFriendNorequest(publicKey))
  }

  override def setTyping(friendNumber: Int, typing: Boolean): Result[ToxSetTypingException.Code, Unit] = {
    catchErrorCode[Unit, ToxSetTypingException.Code, ToxSetTypingException](tox.setTyping(friendNumber, typing))
  }

  override def getFileFileId(friendNumber: Int, fileNumber: Int): Result[ToxFileGetException.Code, ToxFileId] = {
    catchErrorCode[ToxFileId, ToxFileGetException.Code, ToxFileGetException](tox.getFileFileId(friendNumber, fileNumber))
  }

  override def friendSendMessage(friendNumber: Int, messageType: ToxMessageType, timeDelta: Int, message: ToxFriendMessage): Result[ToxFriendSendMessageException.Code, Int] = {
    catchErrorCode[Int, ToxFriendSendMessageException.Code, ToxFriendSendMessageException](tox.friendSendMessage(friendNumber, messageType, timeDelta, message))
  }

  override def friendSendLossyPacket(friendNumber: Int, data: ToxLossyPacket): Result[ToxFriendCustomPacketException.Code, Unit] = {
    catchErrorCode[Unit, ToxFriendCustomPacketException.Code, ToxFriendCustomPacketException](tox.friendSendLossyPacket(friendNumber, data))
  }

  override def bootstrap(address: String, port: Port, publicKey: ToxPublicKey): Result[ToxBootstrapException.Code, Unit] = {
    catchErrorCode[Unit, ToxBootstrapException.Code, ToxBootstrapException](tox.bootstrap(address, port, publicKey))
  }

  override def fileControl(friendNumber: Int, fileNumber: Int, control: ToxFileControl): Result[ToxFileControlException.Code, Unit] = {
    catchErrorCode[Unit, ToxFileControlException.Code, ToxFileControlException](tox.fileControl(friendNumber, fileNumber, control))
  }

  override def friendSendLosslessPacket(friendNumber: Int, data: ToxLosslessPacket): Result[ToxFriendCustomPacketException.Code, Unit] = {
    catchErrorCode[Unit, ToxFriendCustomPacketException.Code, ToxFriendCustomPacketException](tox.friendSendLosslessPacket(friendNumber, data))
  }

  override def setName(name: ToxNickname): Result[ToxSetInfoException.Code, Unit] = {
    catchErrorCode[Unit, ToxSetInfoException.Code, ToxSetInfoException](tox.setName(name))
  }

  override def fileSendChunk(friendNumber: Int, fileNumber: Int, position: Long, data: Array[Byte]): Result[ToxFileSendChunkException.Code, Unit] = {
    catchErrorCode[Unit, ToxFileSendChunkException.Code, ToxFileSendChunkException](tox.fileSendChunk(friendNumber, fileNumber, position, data))
  }

  override def fileSeek(friendNumber: Int, fileNumber: Int, position: Long): Result[ToxFileSeekException.Code, Unit] = {
    catchErrorCode[Unit, ToxFileSeekException.Code, ToxFileSeekException](tox.fileSeek(friendNumber, fileNumber, position))
  }

  override def friendByPublicKey(publicKey: ToxPublicKey): Result[ToxFriendByPublicKeyException.Code, Int] = {
    catchErrorCode[Int, ToxFriendByPublicKeyException.Code, ToxFriendByPublicKeyException](tox.friendByPublicKey(publicKey))
  }

  override def deleteFriend(friendNumber: Int): Result[ToxFriendDeleteException.Code, Unit] = {
    catchErrorCode[Unit, ToxFriendDeleteException.Code, ToxFriendDeleteException](tox.deleteFriend(friendNumber))
  }

  override def fileSend(friendNumber: Int, kind: Int, fileSize: Long, fileId: ToxFileId, filename: ToxFilename): Result[ToxFileSendException.Code, Int] = {
    catchErrorCode[Int, ToxFileSendException.Code, ToxFileSendException](tox.fileSend(friendNumber, kind, fileSize, fileId, filename))
  }

  override def setStatusMessage(message: ToxStatusMessage): Result[ToxSetInfoException.Code, Unit] = {
    catchErrorCode[Unit, ToxSetInfoException.Code, ToxSetInfoException](tox.setStatusMessage(message))
  }

  override def getUdpPort: Result[ToxGetPortException.Code, Port] = {
    catchErrorCode[Port, ToxGetPortException.Code, ToxGetPortException](tox.getUdpPort)
  }

  override def getTcpPort: Result[ToxGetPortException.Code, Port] = {
    catchErrorCode[Port, ToxGetPortException.Code, ToxGetPortException](tox.getTcpPort)
  }

  override def addTcpRelay(address: String, port: Port, publicKey: ToxPublicKey): Result[ToxBootstrapException.Code, Unit] = {
    catchErrorCode[Unit, ToxBootstrapException.Code, ToxBootstrapException](tox.addTcpRelay(address, port, publicKey))
  }

}
