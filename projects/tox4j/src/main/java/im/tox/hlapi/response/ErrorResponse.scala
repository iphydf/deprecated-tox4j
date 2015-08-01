package im.tox.hlapi.response

sealed trait ErrorResponse

object ErrorResponse {
  final case class RequestError() extends ErrorResponse
}
