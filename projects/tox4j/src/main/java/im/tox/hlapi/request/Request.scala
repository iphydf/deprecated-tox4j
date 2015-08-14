package im.tox.hlapi.request

trait Request
object Request {

  final case class GetSelfStatusRequest() extends Request
  final case class GetSelfProfileRequest() extends Request
  final case class GetFriendNameRequest(friendNumber: Int) extends Request
  final case class GetFriendStatusMessageRequest(friendNumber: Int) extends Request
  final case class GetSelfAddressRequest() extends Request
  final case class GetSelfPublicKeyRequest() extends Request
  final case class GetFriendListRequest() extends Request
  final case class GetFriendProfileRequest(friendNumber: Int) extends Request
  final case class GetFriendPublicKeyRequest(friendNumber: Int) extends Request
  final case class GetFriendSentMessageListRequest(friendNumber: Int) extends Request
  final case class GetFriendSentMessageRequest(friendNumber: Int, messageId: Int) extends Request
  final case class GetFriendReceivedMessageListRequest(friendNumber: Int) extends Request
  final case class GetFriendReceivedMessageRequest(friendNumber: Int, messageId: Int) extends Request
  final case class GetFriendConnectionStatusRequest(friendNumber: Int) extends Request
  final case class GetFriendUserStatusRequest(friendNumber: Int) extends Request
}
