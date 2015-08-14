package im.tox.hlapi.request

import im.tox.hlapi.state.ConnectionState.ConnectionStatus
import im.tox.hlapi.state.FriendState.FriendList
import im.tox.hlapi.state.MessageState.{ Message, MessageList }
import im.tox.hlapi.state.PublicKeyState.{ Address, PublicKey }
import im.tox.hlapi.state.UserProfileState.UserProfile
import im.tox.hlapi.state.UserStatusState.UserStatus

trait Reply

object Reply {

  final case class GetSelfStatusReply(userStatus: UserStatus) extends Reply
  final case class GetSelfProfileReply(name: Array[Byte], statusMessage: Array[Byte]) extends Reply
  final case class GetSelfAddressReply(address: Address) extends Reply
  final case class GetSelfPublicKeyReply(publicKey: PublicKey) extends Reply
  final case class GetFriendListReply(friendList: FriendList) extends Reply
  final case class GetFriendProfileReply(name: Array[Byte], statusMessage: Array[Byte]) extends Reply
  final case class GetFriendPublicKeyReply(publicKey: PublicKey) extends Reply
  final case class GetFriendSentMessageListReply(messageList: MessageList) extends Reply
  final case class GetFriendSentMessageReply(message: Message) extends Reply
  final case class GetFriendReceivedMessageListReply(messageList: MessageList) extends Reply
  final case class GetFriendReceivedMessageReply(message: Message) extends Reply
  final case class GetFriendConnectionStatusReply(connectionStatus: ConnectionStatus) extends Reply
  final case class GetFriendUserStatusReply(userStatus: UserStatus) extends Reply
}
