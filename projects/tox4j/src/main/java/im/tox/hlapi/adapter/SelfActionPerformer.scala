package im.tox.hlapi.adapter

import im.tox.hlapi.action.Action
import im.tox.hlapi.action.Action.SelfActionType
import im.tox.hlapi.action.SelfAction.{ GetPublicKeySelfAction, GetMessageListSelfAction, GetFriendListSelfAction }
import im.tox.hlapi.response.InfoResponse.{ GetPublicKeyResponse, GetFriendMessageListResponse, GetFriendListResponse }
import im.tox.hlapi.response.Response
import im.tox.hlapi.response.Response.RequestInfoResponse
import im.tox.hlapi.state.CoreState._
import im.tox.hlapi.state.CoreState.ToxState
import im.tox.hlapi.state.FriendState

import scalaz._

object SelfActionPerformer {

  def performSelfAction(action: SelfActionType): State[ToxState, Response] = State {
    state =>
      {
        action.selfAction match {
          case GetFriendListSelfAction() => (state, RequestInfoResponse(GetFriendListResponse(friendListL.get(state))))
          case GetMessageListSelfAction(friendNumber) => (
            state,
            RequestInfoResponse(GetFriendMessageListResponse(FriendState.friendMessageListL.get(friendsL.get(state)(friendNumber))))
          )
          case GetPublicKeySelfAction() => (
            state,
            RequestInfoResponse(GetPublicKeyResponse(publicKeyL.get(state)))
          )
        }
      }
  }
}
