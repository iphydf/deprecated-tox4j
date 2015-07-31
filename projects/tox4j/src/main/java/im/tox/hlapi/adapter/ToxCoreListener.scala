package im.tox.hlapi.adapter

import im.tox.hlapi.adapter.ToxAdapter.acceptEvent
import im.tox.hlapi.entity.CoreState._
import im.tox.hlapi.entity.Event._
import im.tox.tox4j.core.callbacks.ToxEventListener
import im.tox.tox4j.core.enums.{ ToxUserStatus, ToxMessageType, ToxConnection, ToxFileControl }
import org.jetbrains.annotations.NotNull

class ToxCoreListener(toxClientListener: ToxClientListener) extends ToxEventListener[ToxState] {

  override def selfConnectionStatus(
    @NotNull connectionStatus: ToxConnection
  )(state: ToxState): ToxState = {
    val status = {
      if (connectionStatus == ToxConnection.NONE) {
        Disconnect()
      } else if (connectionStatus == ToxConnection.TCP) {
        Connect(ConnectionOptions().copy(enableUdp = false))
      } else {
        Connect(ConnectionOptions())
      }
    }
    acceptEvent(ReceiveSelfConnectionStatusEvent(status))
    toxClientListener.receiveSelfConnectionStatus(status)
    state
  }

  override def fileRecvControl(
    friendNumber: Int,
    fileNumber: Int, @NotNull control: ToxFileControl
  )(state: ToxState): ToxState = state

  override def fileRecv(
    friendNumber: Int, fileNumber: Int,
    kind: Int, fileSize: Long, @NotNull filename: Array[Byte]
  )(state: ToxState): ToxState = state

  override def fileRecvChunk(
    friendNumber: Int, fileNumber: Int,
    position: Long, @NotNull data: Array[Byte]
  )(state: ToxState): ToxState = state

  override def fileChunkRequest(
    friendNumber: Int, fileNumber: Int,
    position: Long, length: Int
  )(state: ToxState): ToxState = state

  override def friendConnectionStatus(
    friendNumber: Int,
    @NotNull connectionStatus: ToxConnection
  )(state: ToxState): ToxState = {
    val status = {
      if (connectionStatus == ToxConnection.NONE) {
        Disconnect()
      } else if (connectionStatus == ToxConnection.TCP) {
        Connect(connectionOptions = ConnectionOptions().copy(enableUdp = true))
      } else {
        Connect(ConnectionOptions())
      }
    }
    acceptEvent(ReceiveFriendConnectionStatusEvent(friendNumber, status))
    toxClientListener.receiveFriendConnectionStatus(friendNumber, status)
    state
  }

  override def friendMessage(
    friendNumber: Int,
    @NotNull messageType: ToxMessageType, timeDelta: Int, @NotNull message: Array[Byte]
  )(state: ToxState): ToxState = {
    val mtype = {
      if (messageType == ToxMessageType.ACTION) {
        ActionMessage()
      } else {
        NormalMessage()
      }
    }
    acceptEvent(ReceiveFriendMessageEvent(friendNumber, mtype, timeDelta, message))
    toxClientListener.receiveFriendMessage(friendNumber, Message(mtype, timeDelta, message, MessageReceived()))
    state
  }

  override def friendName(
    friendNumber: Int,
    @NotNull name: Array[Byte]
  )(state: ToxState): ToxState = {
    acceptEvent(ReceiveFriendNameEvent(friendNumber, name))
    toxClientListener.receiveFriendName(friendNumber, name)
    state
  }

  override def friendRequest(
    @NotNull publicKey: Array[Byte],
    timeDelta: Int, @NotNull message: Array[Byte]
  )(state: ToxState): ToxState = state

  override def friendStatus(
    friendNumber: Int,
    @NotNull status: ToxUserStatus
  )(state: ToxState): ToxState = {
    val userStatus = {
      if (status == ToxUserStatus.AWAY) {
        Away()
      } else if (status == ToxUserStatus.BUSY) {
        Busy()
      } else {
        Online()
      }
    }
    acceptEvent(ReceiveFriendStatusEvent(friendNumber, userStatus))
    toxClientListener.receiveFriendStatus(friendNumber, userStatus)
    state
  }

  override def friendStatusMessage(
    friendNumber: Int,
    @NotNull message: Array[Byte]
  )(state: ToxState): ToxState = {
    acceptEvent(ReceiveFriendStatusMessageEvent(friendNumber, message))
    toxClientListener.receiveFriendStatusMessage(friendNumber, message)
    state
  }

  override def friendTyping(
    friendNumber: Int,
    isTyping: Boolean
  )(state: ToxState): ToxState = {
    acceptEvent(ReceiveFriendTypingEvent(friendNumber, isTyping))
    toxClientListener.receiveFriendTyping(friendNumber, isTyping)
    state
  }

  override def friendLosslessPacket(
    friendNumber: Int,
    @NotNull data: Array[Byte]
  )(state: ToxState): ToxState = state

  override def friendLossyPacket(
    friendNumber: Int,
    @NotNull data: Array[Byte]
  )(state: ToxState): ToxState = state

  override def friendReadReceipt(
    friendNumber: Int,
    messageId: Int
  )(state: ToxState): ToxState = {
    acceptEvent(ReceiveFriendReadReceiptEvent(friendNumber, messageId))
    toxClientListener.receiveFriendReadReceipt(friendNumber, messageId)
    state
  }

}
