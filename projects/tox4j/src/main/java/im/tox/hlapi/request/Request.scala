package im.tox.hlapi.request

trait Request
object Request {

  final case class GetSelfPublicKeyRequest() extends Request
  final case class GetFriendListRequest() extends Request
}
