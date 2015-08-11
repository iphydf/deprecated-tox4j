package im.tox.hlapi.adapter.parser

import im.tox.hlapi.state.CoreState
import im.tox.hlapi.state.CoreState._
import im.tox.hlapi.state.FriendState.Friend

import scalaz._

abstract class EventParser {

  protected def friendEventHandler[T](friendNumber: Int, state: ToxState,
                            lens: Lens[Friend, T], attribute: T): ToxState = {
    val friend = friendsL.get(state)(friendNumber)
    friendsL.set(
      state,
      friendsL.get(state).updated(
        friendNumber,
        lens.set(friend, attribute)
      )
    )
  }

  protected def friendExist(friendNumber: Int, state: ToxState): Boolean = {
    val friends = CoreState.friendsL.get(state)
    friends.contains(friendNumber)
  }
}
