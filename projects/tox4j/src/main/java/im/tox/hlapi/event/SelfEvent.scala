package im.tox.hlapi.event

import im.tox.hlapi.state.FriendState.Friend

sealed trait SelfEvent

object SelfEvent {

  final case class AddToFriendList(friendNumber: Int, friend: Friend) extends SelfEvent
  final case class GetSelfPublicKeyEvent() extends SelfEvent
}
