package im.tox.hlapi.listener

import im.tox.hlapi.adapter.ToxAdapter
import im.tox.hlapi.event.Event.NetworkEventType
import im.tox.hlapi.event.NetworkEvent
import im.tox.hlapi.event.NetworkEvent._
import im.tox.hlapi.state.ConnectionState.{ Connect, ConnectionOptions, Disconnect }
import im.tox.hlapi.state.FriendState.{ FriendRequest, Friend }
import im.tox.hlapi.state.MessageState.{ ActionMessage, Message, MessageReceived, NormalMessage }
import im.tox.hlapi.state.PublicKeyState.PublicKey
import im.tox.hlapi.state.UserStatusState.{ Away, Busy, Online }
import im.tox.tox4j.core.callbacks.ToxEventListener
import im.tox.tox4j.core.enums.{ ToxConnection, ToxFileControl, ToxMessageType, ToxUserStatus }
import org.jetbrains.annotations.NotNull

class ToxCoreListener[Seq[NetworkEvent]] extends ToxEventListener[Seq[NetworkEvent]] {

  override def selfConnectionStatus(
    @NotNull connectionStatus: ToxConnection
  )(eventList: Seq[NetworkEvent]): Seq[NetworkEvent] = {
    val status = {
      if (connectionStatus == ToxConnection.NONE) {
        Disconnect()
      } else if (connectionStatus == ToxConnection.TCP) {
        Connect(ConnectionOptions().copy(enableUdp = false))
      } else {
        Connect(ConnectionOptions())
      }
    }
    eventList :+ ReceiveSelfConnectionStatusEvent(status)
  }

  override def fileRecvControl(
    friendNumber: Int,
    fileNumber: Int, @NotNull control: ToxFileControl
  )(eventList: Seq[NetworkEvent]): Seq[NetworkEvent] = eventList

  override def fileRecv(
    friendNumber: Int, fileNumber: Int,
    kind: Int, fileSize: Long, @NotNull filename: Array[Byte]
  )(eventList: Seq[NetworkEvent]): Seq[NetworkEvent] = eventList

  override def fileRecvChunk(
    friendNumber: Int, fileNumber: Int,
    position: Long, @NotNull data: Array[Byte]
  )(eventList: Seq[NetworkEvent]): Seq[NetworkEvent] = eventList

  override def fileChunkRequest(
    friendNumber: Int, fileNumber: Int,
    position: Long, length: Int
  )(eventList: Seq[NetworkEvent]): Seq[NetworkEvent] = eventList

  override def friendConnectionStatus(
    friendNumber: Int,
    @NotNull connectionStatus: ToxConnection
  )(eventList: Seq[NetworkEvent]): Seq[NetworkEvent] = {
    val status = {
      if (connectionStatus == ToxConnection.NONE) {
        Disconnect()
      } else if (connectionStatus == ToxConnection.TCP) {
        Connect(connectionOptions = ConnectionOptions().copy(enableUdp = true))
      } else {
        Connect(ConnectionOptions())
      }
    }
    eventList :+ ReceiveFriendConnectionStatusEvent(friendNumber, status)
  }

  override def friendMessage(
    friendNumber: Int,
    @NotNull messageType: ToxMessageType, timeDelta: Int, @NotNull message: Array[Byte]
  )(eventList: Seq[NetworkEvent]): Seq[NetworkEvent] = {
    val mtype = {
      if (messageType == ToxMessageType.ACTION) {
        ActionMessage()
      } else {
        NormalMessage()
      }
    }
    eventList :+ ReceiveFriendMessageEvent(friendNumber, mtype, timeDelta, message)
  }

  override def friendName(
    friendNumber: Int,
    @NotNull name: Array[Byte]
  )(eventList: Seq[NetworkEvent]): Seq[NetworkEvent] = {
    eventList :+ ReceiveFriendNameEvent(friendNumber, name)
  }

  override def friendRequest(
    @NotNull publicKey: Array[Byte],
    timeDelta: Int, @NotNull message: Array[Byte]
  )(eventList: Seq[NetworkEvent]): Seq[NetworkEvent] = {
    eventList :+ ReceiveFriendRequestEvent(PublicKey(publicKey), FriendRequest(message, timeDelta))
  }

  override def friendStatus(
    friendNumber: Int,
    @NotNull status: ToxUserStatus
  )(eventList: Seq[NetworkEvent]): Seq[NetworkEvent] = {
    val userStatus = {
      if (status == ToxUserStatus.AWAY) {
        Away()
      } else if (status == ToxUserStatus.BUSY) {
        Busy()
      } else {
        Online()
      }
    }
    eventList :+ ReceiveFriendStatusEvent(friendNumber, userStatus)
  }

  override def friendStatusMessage(
    friendNumber: Int,
    @NotNull message: Array[Byte]
  )(eventList: Seq[NetworkEvent]): Seq[NetworkEvent] = {
    eventList :+ ReceiveFriendStatusMessageEvent(friendNumber, message)
  }

  override def friendTyping(
    friendNumber: Int,
    isTyping: Boolean
  )(eventList: Seq[NetworkEvent]): Seq[NetworkEvent] = {
    eventList :+ ReceiveFriendTypingEvent(friendNumber, isTyping)
  }

  override def friendLosslessPacket(
    friendNumber: Int,
    @NotNull data: Array[Byte]
  )(eventList: Seq[NetworkEvent]): Seq[NetworkEvent] = eventList

  override def friendLossyPacket(
    friendNumber: Int,
    @NotNull data: Array[Byte]
  )(eventList: Seq[NetworkEvent]): Seq[NetworkEvent] = eventList

  override def friendReadReceipt(
    friendNumber: Int,
    messageId: Int
  )(eventList: Seq[NetworkEvent]): Seq[NetworkEvent] = {
    eventList :+ ReceiveFriendReadReceiptEvent(friendNumber, messageId)
  }

}
