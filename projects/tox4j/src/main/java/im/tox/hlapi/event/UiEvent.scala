package im.tox.hlapi.event

import im.tox.hlapi.listener.ToxClientListener
import im.tox.hlapi.state.ConnectionState.ConnectionStatus
import im.tox.hlapi.state.FileState.File
import im.tox.hlapi.state.FriendState.FriendRequestMessage
import im.tox.hlapi.state.MessageState.Message
import im.tox.hlapi.state.PublicKeyState.PublicKey
import im.tox.hlapi.state.UserStatusState.UserStatus

sealed trait UiEvent

object UiEvent {

  final case class RegisterEventListener(toxClientListener: ToxClientListener) extends UiEvent
  //  Send a friend request
  final case class SendFriendRequestEvent(publicKey: PublicKey, request: Option[FriendRequestMessage]) extends UiEvent
  //  Delete a friend
  final case class DeleteFriendEvent(friendNumber: Int) extends UiEvent
  //  Change user nickname
  final case class SetNicknameEvent(nickname: Array[Byte]) extends UiEvent
  //  Send a text message to a private conversation
  final case class SendFriendMessageEvent(friendNumber: Int, message: Message) extends UiEvent
  //  Initiate a file transmission request to a friend
  final case class SendFileTransmissionRequestEvent(friend: Int, file: File) extends UiEvent
  //  Get all conversations
  final case class GetPrivateConversationList() extends UiEvent
  //  Get all friends
  final case class GetFriendList() extends UiEvent
  //  Set status message
  final case class SetStatusMessageEvent(statusMessage: Array[Byte]) extends UiEvent
  //  Change self user status
  final case class SetUserStatusEvent(status: UserStatus) extends UiEvent
  //  Change self connection
  final case class SetConnectionStatusEvent(status: ConnectionStatus) extends UiEvent
  //  Get the messages associated with a conversation
  final case class GetMessageList(friendNumber: Int) extends UiEvent
  //  Get the file sent history with a friend
  final case class GetFileList(friendNumber: Int) extends UiEvent
  final case class GetPublicKeyEvent() extends UiEvent

}
