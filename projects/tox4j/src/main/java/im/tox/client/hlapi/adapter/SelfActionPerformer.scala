package im.tox.client.hlapi.adapter

import im.tox.client.hlapi.entity.CoreState._
import im.tox.client.hlapi.entity.Action
import im.tox.client.hlapi.entity.Action.{ GetMessageListSelfAction, GetFriendListSelfAction }
import im.tox.client.hlapi.entity.CoreState.{ Gettable, ToxState }

import scalaz._

object SelfActionPerformer {

  def performSelfAction(action: Option[Action]): State[ToxState, Option[Gettable]] = State {

    state =>
      {
        action match {
          case Some(GetFriendListSelfAction()) => (state, Some(friendListL.get(state)))
          case Some(GetMessageListSelfAction(friendNumber)) => (
            state,
            Some(friendMessageListL.get(friendsL.get(state)(friendNumber)))
          )
        }
      }
  }
}
