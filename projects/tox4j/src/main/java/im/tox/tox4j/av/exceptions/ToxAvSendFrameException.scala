package im.tox.tox4j.av.exceptions

import im.tox.tox4j.exceptions.ToxException

object ToxAvSendFrameException {

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
   * One or more of the frame parameters was invalid. E.g. the resolution may be too
   * small or too large, or the audio sampling rate may be unsupported.
   */
  case object INVALID extends Code
  /**
   * In case of video, one of Y, U, or V was NULL. In case of audio, the samples
   * data pointer was NULL.
   */
  case object NULL extends Code
  /**
   * Failed to push frame through rtp interface.
   */
  case object RTP_FAILED extends Code

}

final case class ToxAvSendFrameException(code: ToxAvSendFrameException.Code, message: String = "")
  extends ToxException[ToxAvSendFrameException.Code](message)
