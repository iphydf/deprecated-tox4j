package im.tox.hlapi.adapter

import im.tox.hlapi.request.Reply.{ GetFriendListReply, GetSelfPublicKeyReply }
import im.tox.hlapi.request.Request.{ GetFriendListRequest, GetSelfPublicKeyRequest }
import im.tox.hlapi.request.{ Reply, Request }
import im.tox.hlapi.state.CoreState
import im.tox.hlapi.state.CoreState.ToxState

object RequestParser {

  def parseRequest(request: Request, state: ToxState): Reply = {
    request match {
      case GetSelfPublicKeyRequest() => GetSelfPublicKeyReply(CoreState.publicKeyL.get(state))
      case GetFriendListRequest()    => GetFriendListReply(CoreState.friendListL.get(state))
    }
  }
}
