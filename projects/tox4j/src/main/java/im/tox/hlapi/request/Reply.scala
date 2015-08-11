package im.tox.hlapi.request

import im.tox.hlapi.state.FriendState.FriendList
import im.tox.hlapi.state.PublicKeyState.{ Address, PublicKey }
import im.tox.hlapi.state.UserProfileState.UserProfile

trait Reply

object Reply {

  final case class GetSelfProfileReply(userProfile: UserProfile) extends Reply
  final case class GetSelfAddressReply(address: Address) extends Reply
  final case class GetSelfPublicKeyReply(publicKey: PublicKey) extends Reply
  final case class GetFriendListReply(friendList: FriendList) extends Reply
}
