package im.tox.hlapi.response

import im.tox.hlapi.state.FriendState.FriendList
import im.tox.hlapi.state.MessageState.MessageList
import im.tox.hlapi.state.PublicKeyState.PublicKey

sealed trait InfoResponse

object InfoResponse {
  final case class GetFriendListResponse(friendList: FriendList) extends InfoResponse
  final case class GetFriendMessageListResponse(messageList: MessageList) extends InfoResponse
  final case class GetPublicKeyResponse(publicKey: PublicKey) extends InfoResponse
}
