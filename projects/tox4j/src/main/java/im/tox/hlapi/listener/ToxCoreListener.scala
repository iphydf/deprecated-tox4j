package im.tox.hlapi.listener

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

import scala.collection.immutable.Queue

object ToxCoreListener extends ToxEventListener[Queue[NetworkEvent]] {

  override def selfConnectionStatus(
    @NotNull connectionStatus: ToxConnection
  )(eventList: Queue[NetworkEvent]): Queue[NetworkEvent] = {
    val status = {
      if (connectionStatus == ToxConnection.NONE) {
        Disconnect()
      } else if (connectionStatus == ToxConnection.TCP) {
        Connect(ConnectionOptions().copy(enableUdp = false))
      } else {
        Connect(ConnectionOptions())
      }
    }
    println(eventList)
    eventList.enqueue(ReceiveSelfConnectionStatusEvent(status))
  }

  override def fileRecvControl(
    friendNumber: Int,
    fileNumber: Int, @NotNull control: ToxFileControl
  )(eventList: Queue[NetworkEvent]): Queue[NetworkEvent] = eventList

  override def fileRecv(
    friendNumber: Int, fileNumber: Int,
    kind: Int, fileSize: Long, @NotNull filename: Array[Byte]
  )(eventList: Queue[NetworkEvent]): Queue[NetworkEvent] = eventList

  override def fileRecvChunk(
    friendNumber: Int, fileNumber: Int,
    position: Long, @NotNull data: Array[Byte]
  )(eventList: Queue[NetworkEvent]): Queue[NetworkEvent] = eventList

  override def fileChunkRequest(
    friendNumber: Int, fileNumber: Int,
    position: Long, length: Int
  )(eventList: Queue[NetworkEvent]): Queue[NetworkEvent] = eventList

  override def friendConnectionStatus(
    friendNumber: Int,
    @NotNull connectionStatus: ToxConnection
  )(eventList: Queue[NetworkEvent]): Queue[NetworkEvent] = {
    val status = {
      if (connectionStatus == ToxConnection.NONE) {
        Disconnect()
      } else if (connectionStatus == ToxConnection.TCP) {
        Connect(connectionOptions = ConnectionOptions().copy(enableUdp = true))
      } else {
        Connect(ConnectionOptions())
      }
    }
    eventList.enqueue(ReceiveFriendConnectionStatusEvent(friendNumber, status))
  }

  override def friendMessage(
    friendNumber: Int,
    @NotNull messageType: ToxMessageType, timeDelta: Int, @NotNull message: Array[Byte]
  )(eventList: Queue[NetworkEvent]): Queue[NetworkEvent] = {
    val mtype = {
      if (messageType == ToxMessageType.ACTION) {
        ActionMessage()
      } else {
        NormalMessage()
      }
    }
    val mes = Message(mtype, timeDelta, message, MessageReceived())
    eventList.enqueue(ReceiveFriendMessageEvent(friendNumber, mes))
  }

  override def friendName(
    friendNumber: Int,
    @NotNull name: Array[Byte]
  )(eventList: Queue[NetworkEvent]): Queue[NetworkEvent] = {
    eventList.enqueue(ReceiveFriendNameEvent(friendNumber, name))
  }

  override def friendRequest(
    @NotNull publicKey: Array[Byte],
    timeDelta: Int, @NotNull message: Array[Byte]
  )(eventList: Queue[NetworkEvent]): Queue[NetworkEvent] = {
    eventList.enqueue(ReceiveFriendRequestEvent(PublicKey(publicKey), FriendRequest(message, timeDelta)))
  }

  override def friendStatus(
    friendNumber: Int,
    @NotNull status: ToxUserStatus
  )(eventList: Queue[NetworkEvent]): Queue[NetworkEvent] = {
    val userStatus = {
      if (status == ToxUserStatus.AWAY) {
        Away()
      } else if (status == ToxUserStatus.BUSY) {
        Busy()
      } else {
        Online()
      }
    }
    eventList.enqueue(ReceiveFriendStatusEvent(friendNumber, userStatus))
  }

  override def friendStatusMessage(
    friendNumber: Int,
    @NotNull message: Array[Byte]
  )(eventList: Queue[NetworkEvent]): Queue[NetworkEvent] = {
    eventList.enqueue(ReceiveFriendStatusMessageEvent(friendNumber, message))
  }

  override def friendTyping(
    friendNumber: Int,
    isTyping: Boolean
  )(eventList: Queue[NetworkEvent]): Queue[NetworkEvent] = {
    eventList.enqueue(ReceiveFriendTypingEvent(friendNumber, isTyping))
  }

  override def friendLosslessPacket(
    friendNumber: Int,
    @NotNull data: Array[Byte]
  )(eventList: Queue[NetworkEvent]): Queue[NetworkEvent] = eventList

  override def friendLossyPacket(
    friendNumber: Int,
    @NotNull data: Array[Byte]
  )(eventList: Queue[NetworkEvent]): Queue[NetworkEvent] = eventList

  override def friendReadReceipt(
    friendNumber: Int,
    messageId: Int
  )(eventList: Queue[NetworkEvent]): Queue[NetworkEvent] = {
    eventList.enqueue(ReceiveFriendReadReceiptEvent(friendNumber, messageId))
  }

}
