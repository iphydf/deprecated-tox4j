package im.tox.hlapi.response

trait Response

object Response {

  final case class RequestSuccessResponse(requestSuccess: SuccessResponse) extends Response
  final case class RequestErrorResponse(requestError: ErrorResponse) extends Response

}
