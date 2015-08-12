package im.tox.hlapi.adapter.parser

import im.tox.hlapi.request.Reply.{ GetFriendListReply, GetSelfAddressReply, GetSelfProfileReply, GetSelfPublicKeyReply }
import im.tox.hlapi.request.Request.{ GetFriendListRequest, GetSelfAddressRequest, GetSelfProfileRequest, GetSelfPublicKeyRequest }
import im.tox.hlapi.request.{ Reply, Request }
import im.tox.hlapi.state.CoreState
import im.tox.hlapi.state.CoreState.ToxState

object RequestParser {

  def parseRequest(request: Request, state: ToxState): Reply = {
    request match {
      case GetSelfProfileRequest()   => GetSelfProfileReply(CoreState.userProfileL.get(state))
      case GetSelfAddressRequest()   => GetSelfAddressReply(CoreState.addressL.get(state))
      case GetSelfPublicKeyRequest() => GetSelfPublicKeyReply(CoreState.publicKeyL.get(state))
      case GetFriendListRequest()    => GetFriendListReply(CoreState.friendListL.get(state))
    }
  }
}
