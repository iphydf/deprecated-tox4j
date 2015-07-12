package im.tox.tox4j.av.exceptions

import im.tox.tox4j.exceptions.ToxException

object ToxAvCallControlException {

  sealed trait Code extends ToxException.Code
  /**
   * The friend number did not designate a valid friend.
   */
  case object FRIEND_NOT_FOUND extends Code
  /**
   * This client is currently not in a call with the friend. Before the call is
   * answered, only CANCEL is a valid control
   */
  case object FRIEND_NOT_IN_CALL extends Code
  /**
   * Happens if user tried to pause an already paused call or if trying to
   * resume a call that is not paused.
   */
  case object INVALID_TRANSITION extends Code

}

final case class ToxAvCallControlException(code: ToxAvCallControlException.Code, message: String = "")
  extends ToxException[ToxAvCallControlException.Code](message)
