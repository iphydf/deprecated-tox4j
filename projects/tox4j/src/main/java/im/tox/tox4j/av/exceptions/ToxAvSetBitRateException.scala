package im.tox.tox4j.av.exceptions

import im.tox.tox4j.exceptions.ToxException

object ToxAvSetBitRateException {

  sealed trait Code extends ToxException.Code
  /**
   * The friend number did not designate a valid friend.
   */
  case object FRIEND_NOT_FOUND extends Code
  /**
   * This client is currently not in a call with the friend.
   */
  case object FRIEND_NOT_IN_CALL extends Code
  /**
   * The bit rate passed was not one of the supported values.
   */
  case object INVALID extends Code

}

final case class ToxAvSetBitRateException(code: ToxAvSetBitRateException.Code, message: String = "")
  extends ToxException[ToxAvSetBitRateException.Code](message)
