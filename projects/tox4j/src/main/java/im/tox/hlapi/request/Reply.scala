package im.tox.hlapi.request

import im.tox.hlapi.state.FriendState.FriendList
import im.tox.hlapi.state.PublicKeyState.PublicKey

trait Reply

object Reply {

  final case class GetSelfPublicKeyReply(publicKey: PublicKey) extends Reply
  final case class GetFriendListReply(friendList: FriendList) extends Reply
}
