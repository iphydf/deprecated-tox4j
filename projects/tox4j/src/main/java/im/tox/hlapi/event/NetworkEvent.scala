package im.tox.hlapi.event

import im.tox.hlapi.state.ConnectionState.ConnectionStatus
import im.tox.hlapi.state.MessageState.MessageType
import im.tox.hlapi.state.UserStatusState.UserStatus

sealed trait NetworkEvent

object NetworkEvent {

  //  Self connection status (connected/disconnected)
  final case class ReceiveSelfConnectionStatusEvent(connectionStatus: ConnectionStatus) extends NetworkEvent
  //  Receive file transmission control from friends
  final case class ReceiveFileTransmissionControlEvent() extends NetworkEvent
  //  Receive file transmission request from friends
  final case class ReceiveFileTransmissionRequestEvent() extends NetworkEvent
  //  Receive a chunk of file under transmission from friends
  final case class ReceiveFileChunkEvent() extends NetworkEvent
  //  A friend’s connection status changes (online/offline)
  final case class ReceiveFriendConnectionStatusEvent(friendNumber: Int, connectionStatus: ConnectionStatus) extends NetworkEvent
  //  Receive a message from a friend
  final case class ReceiveFriendMessageEvent(friendNumber: Int, messageType: MessageType, timeDelta: Int, content: Array[Byte]) extends NetworkEvent
  //  A friend’s name changes
  final case class ReceiveFriendNameEvent(friendNumber: Int, name: Array[Byte]) extends NetworkEvent
  //  Receive a friend request
  final case class ReceiveFriendRequestEvent() extends NetworkEvent
  //  A friend’s user status changes
  final case class ReceiveFriendStatusEvent(friendNumber: Int, userStatus: UserStatus) extends NetworkEvent
  //  A friend’s status message changes
  final case class ReceiveFriendStatusMessageEvent(friendNumber: Int, statusMessage: Array[Byte]) extends NetworkEvent
  //  A friend typing status changes
  final case class ReceiveFriendTypingEvent(friendNumber: Int, isTyping: Boolean) extends NetworkEvent
  //  A lossy packet arrives
  final case class ReceiveLossyPacketEvent() extends NetworkEvent
  //  A lossless packet arrives
  final case class ReceiveLosslessPacketEvent() extends NetworkEvent
  //  Receive the read receipt of a message
  final case class ReceiveFriendReadReceiptEvent(friendNumber: Int, messageId: Int) extends NetworkEvent

}
