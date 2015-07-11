package im.tox.tox4j.core.exceptions

import im.tox.tox4j.exceptions.ToxException

object ToxFileSendException {

  sealed trait Code extends ToxException.Code
  /**
   * This client is currently not connected to the friend.
   */
  case object FRIEND_NOT_CONNECTED extends Code
  /**
   * The friendNumber passed did not designate a valid friend.
   */
  case object FRIEND_NOT_FOUND extends Code
  /**
   * Filename length exceeded [[ToxCoreConstants.MAX_FILENAME_LENGTH]] bytes.
   */
  case object NAME_TOO_LONG extends Code
  /**
   * An argument was null.
   */
  case object NULL extends Code
  /**
   * Too many ongoing transfers. The maximum number of concurrent file transfers
   * is 256 per friend per direction (sending and receiving, so 512 total).
   */
  case object TOO_MANY extends Code

}

final case class ToxFileSendException(code: ToxFileSendException.Code, message: String = "")
  extends ToxException[ToxFileSendException.Code](message)
