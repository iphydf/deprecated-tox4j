package im.tox.tox4j.av.exceptions

import im.tox.tox4j.exceptions.ToxException

object ToxAvCallException {

  sealed trait Code extends ToxException.Code
  /**
   * Attempted to call a friend while already in an audio or video call with them.
   */
  case object FRIEND_ALREADY_IN_CALL extends Code
  /**
   * The friend was valid, but not currently connected.
   */
  case object FRIEND_NOT_CONNECTED extends Code
  /**
   * The friend number did not designate a valid friend.
   */
  case object FRIEND_NOT_FOUND extends Code
  /**
   * Audio or video bit rate is invalid.
   */
  case object INVALID_BIT_RATE extends Code
  /**
   * A memory allocation error occurred.
   */
  case object MALLOC extends Code

}

final case class ToxAvCallException(code: ToxAvCallException.Code, message: String = "")
  extends ToxException[ToxAvCallException.Code](message)
