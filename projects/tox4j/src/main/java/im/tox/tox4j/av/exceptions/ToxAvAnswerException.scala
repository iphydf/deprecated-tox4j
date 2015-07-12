package im.tox.tox4j.av.exceptions

import im.tox.tox4j.exceptions.ToxException

object ToxAvAnswerException {

  sealed trait Code extends ToxException.Code
  /**
   * Failed to initialise codecs for call session.
   */
  case object CODEC_INITIALIZATION extends Code
  /**
   * The friend was valid, but they are not currently trying to initiate a call.
   * This is also returned if this client is already in a call with the friend.
   */
  case object FRIEND_NOT_CALLING extends Code
  /**
   * The friend number did not designate a valid friend.
   */
  case object FRIEND_NOT_FOUND extends Code
  /**
   * Audio or video bit rate is invalid.
   */
  case object INVALID_BIT_RATE extends Code

}

final case class ToxAvAnswerException(code: ToxAvAnswerException.Code, message: String = "")
  extends ToxException[ToxAvAnswerException.Code](message)
