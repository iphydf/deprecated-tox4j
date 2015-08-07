package im.tox.hlapi.action

import im.tox.hlapi.listener.ToxClientListener
import im.tox.hlapi.state.ConnectionState.{ ConnectionOptions, ConnectionStatus }
import im.tox.hlapi.state.FileState.File
import im.tox.hlapi.state.FriendState.FriendRequestMessage
import im.tox.hlapi.state.MessageState.Message
import im.tox.hlapi.state.PublicKeyState.PublicKey
import im.tox.hlapi.state.UserStatusState.UserStatus

sealed trait NetworkAction

object NetworkAction {

  final case class AddFriendNoRequestAction(publicKey: PublicKey) extends NetworkAction
  final case class SetNameAction(nickname: Array[Byte]) extends NetworkAction
  final case class SetStatusMessageAction(statusMessage: Array[Byte]) extends NetworkAction
  final case class SetUserStatusAction(userStatus: UserStatus) extends NetworkAction
  final case class SetConnectionStatusAction(connectionStatus: ConnectionStatus) extends NetworkAction
  final case class SendFriendRequestAction(publicKey: PublicKey, requestMessage: FriendRequestMessage) extends NetworkAction
  final case class DeleteFriend(friendNumber: Int) extends NetworkAction
  final case class SendFriendMessageAction(friendNumber: Int, message: Message) extends NetworkAction
  final case class GetFriendPublicKeyAction(friendNumber: Int) extends NetworkAction
  final case class GetSelfPublicKeyAction() extends NetworkAction
  final case class ToxInitAction(connectionOptions: ConnectionOptions, toxClientListener: ToxClientListener) extends NetworkAction
  final case class ToxEndAction() extends NetworkAction
  final case class RegisterEventListenerAction(eventListener: ToxClientListener) extends NetworkAction
  final case class SendFileTransmissionRequestAction(friendNumber: Int, file: File) extends NetworkAction

}
