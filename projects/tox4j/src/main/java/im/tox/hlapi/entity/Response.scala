package im.tox.hlapi.entity

import im.tox.hlapi.entity.CoreState._

trait Response

object Response {

  final case class Success() extends Response
  sealed trait Error extends Response
  sealed trait RequestInfo extends Response

  final case class GetFriendListResponse(friendList: FriendList) extends RequestInfo
  final case class GetFriendMessageListResponse(messageList: MessageList) extends RequestInfo
  final case class GetPublicKeyResponse(publicKey: PublicKey) extends RequestInfo
}
