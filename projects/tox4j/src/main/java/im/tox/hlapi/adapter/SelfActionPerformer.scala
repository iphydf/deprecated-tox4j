package im.tox.hlapi.adapter

import im.tox.hlapi.entity.CoreState._
import im.tox.hlapi.entity.Action
import im.tox.hlapi.entity.Action.{ GetPublicKeySelfAction, GetMessageListSelfAction, GetFriendListSelfAction }
import im.tox.hlapi.entity.CoreState.ToxState
import im.tox.hlapi.entity.Event.{ ReplyGetPublicKeyRequest, ReplyGetFriendMessageListRequest, ReplyGetFriendListRequest, ReplyEvent }

import scalaz._

object SelfActionPerformer {

  def performSelfAction(action: Action): State[ToxState, ReplyEvent] = State {
    state =>
      {
        action match {
          case GetFriendListSelfAction() => (state, ReplyGetFriendListRequest(friendListL.get(state)))
          case GetMessageListSelfAction(friendNumber) => (
            state,
            ReplyGetFriendMessageListRequest(friendMessageListL.get(friendsL.get(state)(friendNumber)))
          )
          case GetPublicKeySelfAction() => (
            state,
            ReplyGetPublicKeyRequest(publicKeyL.get(state).key)
          )
        }
      }
  }
}
