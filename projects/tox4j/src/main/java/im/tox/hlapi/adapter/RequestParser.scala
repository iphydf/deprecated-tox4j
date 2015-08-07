package im.tox.hlapi.adapter

import im.tox.hlapi.request.Reply.GetSelfPublicKeyReply
import im.tox.hlapi.request.Request.GetSelfPublicKeyRequest
import im.tox.hlapi.request.{ Reply, Request }
import im.tox.hlapi.state.CoreState
import im.tox.hlapi.state.CoreState.ToxState

object RequestParser {

  def parseRequest(request: Request, state: ToxState): Reply = {
    request match {
      case GetSelfPublicKeyRequest() => GetSelfPublicKeyReply(CoreState.publicKeyL.get(state))
    }
  }
}
