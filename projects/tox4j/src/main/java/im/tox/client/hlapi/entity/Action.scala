package im.tox.client.hlapi.entity

import im.tox.tox4j.core.callbacks.ToxEventListener
import im.tox.client.hlapi.entity.CoreState._

sealed trait Action

object Action {

  sealed abstract class NetworkAction extends Action
  final case class SetNameAction(nickname: String) extends NetworkAction
  final case class SetStatusMessageAction(statusMessage: String) extends NetworkAction
  final case class SetUserStatusAction(userStatus: UserStatus) extends NetworkAction
  final case class SetConnectionStatusAction(connectionStatus: ConnectionStatus) extends NetworkAction
  final case class SendFriendRequestAction(publicKey: Array[Byte], requestMessage: Option[Array[Byte]]) extends NetworkAction
  final case class deleteFriend(friendNumber: Int) extends NetworkAction
  final case class SendFriendMessageAction(friendNumber: Int, message: Message) extends NetworkAction
  final case class GetFriendPublicKeyAction(friendNumber: Int) extends NetworkAction
  final case class GetSelfPublicKeyAction() extends NetworkAction
  final case class RegisterEventListenerAction(eventListener: ToxEventListener[Unit]) extends NetworkAction

  sealed abstract class SelfAction extends Action
  final case class GetFriendListSelfAction() extends SelfAction
  final case class GetMessageListSelfAction(friendNumber: Int) extends SelfAction
  final case class GetFileSentListSelfAction(friendNumber: Int) extends SelfAction

}
