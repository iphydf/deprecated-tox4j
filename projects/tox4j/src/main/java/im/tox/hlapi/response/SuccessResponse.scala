package im.tox.hlapi.response

sealed trait SuccessResponse

object SuccessResponse {
  final case class RequestSuccess() extends SuccessResponse
}
