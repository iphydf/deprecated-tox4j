package im.tox.hlapi.action

sealed trait SelfAction

object SelfAction {

  final case class GetFriendListSelfAction() extends SelfAction
  final case class GetMessageListSelfAction(friendNumber: Int) extends SelfAction
  final case class GetFileSentListSelfAction(friendNumber: Int) extends SelfAction
  final case class GetPublicKeySelfAction() extends SelfAction

}
