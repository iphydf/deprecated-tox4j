package im.tox.hlapi.adapter

import im.tox.hlapi.entity.CoreState._
import im.tox.hlapi.entity.Response.{ GetPublicKeyResponse, GetFriendMessageListResponse, GetFriendListResponse }
import im.tox.hlapi.entity.{ Response, Action }
import im.tox.hlapi.entity.Action.{ GetPublicKeySelfAction, GetMessageListSelfAction, GetFriendListSelfAction }
import im.tox.hlapi.entity.CoreState.ToxState
import im.tox.hlapi.entity.Event.{ ReplyGetPublicKeyRequest, ReplyGetFriendMessageListRequest, ReplyGetFriendListRequest, ReplyEvent }

import scalaz._

object SelfActionPerformer {

  def performSelfAction(action: Action): State[ToxState, Response] = State {
    state =>
      {
        action match {
          case GetFriendListSelfAction() => (state, GetFriendListResponse(friendListL.get(state)))
          case GetMessageListSelfAction(friendNumber) => (
            state,
            GetFriendMessageListResponse(friendMessageListL.get(friendsL.get(state)(friendNumber)))
          )
          case GetPublicKeySelfAction() => (
            state,
            GetPublicKeyResponse(publicKeyL.get(state))
          )
        }
      }
  }
}
