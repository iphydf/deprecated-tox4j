package im.tox.hlapi.entity

import im.tox.hlapi.adapter.ToxClientListener
import im.tox.hlapi.entity.CoreState._

sealed trait Action

object Action {

  sealed abstract class NetworkAction extends Action
  final case class SetNameAction(nickname: Array[Byte]) extends NetworkAction
  final case class SetStatusMessageAction(statusMessage: Array[Byte]) extends NetworkAction
  final case class SetUserStatusAction(userStatus: UserStatus) extends NetworkAction
  final case class SetConnectionStatusAction(connectionStatus: ConnectionStatus) extends NetworkAction
  final case class SendFriendRequestAction(publicKey: PublicKey, requestMessage: Option[FriendRequestMessage]) extends NetworkAction
  final case class deleteFriend(friendNumber: Int) extends NetworkAction
  final case class SendFriendMessageAction(friendNumber: Int, message: Message) extends NetworkAction
  final case class GetFriendPublicKeyAction(friendNumber: Int) extends NetworkAction
  final case class GetSelfPublicKeyAction() extends NetworkAction
  final case class RegisterEventListenerAction(eventListener: ToxClientListener) extends NetworkAction
  final case class SendFileTransmissionRequestAction(friendNumber: Int, file: File) extends NetworkAction

  sealed abstract class SelfAction extends Action
  final case class GetFriendListSelfAction() extends SelfAction
  final case class GetMessageListSelfAction(friendNumber: Int) extends SelfAction
  final case class GetFileSentListSelfAction(friendNumber: Int) extends SelfAction
  final case class GetPublicKeySelfAction() extends SelfAction

  final case class NoAction() extends Action

}
