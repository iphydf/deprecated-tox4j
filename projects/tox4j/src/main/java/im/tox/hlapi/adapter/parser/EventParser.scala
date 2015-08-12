package im.tox.hlapi.adapter.parser

import im.tox.hlapi.state.CoreState
import im.tox.hlapi.state.CoreState._
import im.tox.hlapi.state.FriendState.Friend

import scalaz._

abstract class EventParser {

  protected def friendExist(friendNumber: Int, state: ToxState): Boolean = {
    val friends = CoreState.friendsL.get(state)
    friends.contains(friendNumber)
  }
}
