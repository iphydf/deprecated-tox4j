package im.tox.hlapi.event

import im.tox.hlapi.listener.ToxClientListener
import im.tox.hlapi.state.ConnectionState.{ ConnectionOptions, ConnectionStatus }
import im.tox.hlapi.state.FileState.File
import im.tox.hlapi.state.FriendState.FriendRequest
import im.tox.hlapi.state.MessageState.Message
import im.tox.hlapi.state.PublicKeyState.{ Address, PublicKey }
import im.tox.hlapi.state.UserStatusState.UserStatus

sealed trait UiEvent

object UiEvent {

  //  Send a friend request
  final case class SendFriendRequestEvent(address: Address, request: FriendRequest) extends UiEvent
  //  Delete a friend
  final case class DeleteFriendEvent(friendNumber: Int) extends UiEvent
  //  Change user nickname
  final case class SetNicknameEvent(nickname: Array[Byte]) extends UiEvent
  //  Send a text message to a private conversation
  final case class SendFriendMessageEvent(friendNumber: Int, message: Array[Byte]) extends UiEvent
  //  Initiate a file transmission request to a friend
  final case class SendFileTransmissionRequestEvent(friend: Int, file: File) extends UiEvent
  //  Set status message
  final case class SetStatusMessageEvent(statusMessage: Array[Byte]) extends UiEvent
  //  Change self user status
  final case class SetUserStatusEvent(status: UserStatus) extends UiEvent
  // Initiate a tox session
  final case class ToxInitEvent(options: ConnectionOptions, toxClientListener: ToxClientListener) extends UiEvent
  // End a tox session
  final case class ToxEndEvent() extends UiEvent
  final case class AddFriendNoRequestEvent(publicKey: PublicKey) extends UiEvent

}
