package im.tox.tox4j.core.exceptions

import im.tox.tox4j.exceptions.ToxException

object ToxFileSendChunkException {

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
   * Attempted to send more or less data than requested. The requested data size is
   * adjusted according to maximum transmission unit and the expected end of
   * the file. Trying to send less or more than requested will return this error.
   */
  case object INVALID_LENGTH extends Code
  /**
   * No file transfer with the given file number was found for the given friend.
   */
  case object NOT_FOUND extends Code
  /**
   * File transfer was found but isn't in a transferring state: (paused, done,
   * broken, etc...) (happens only when not called from the request chunk callback).
   */
  case object NOT_TRANSFERRING extends Code
  /**
   * An argument was null.
   */
  case object NULL extends Code
  /**
   * An allocation error occurred while increasing the send queue size.
   */
  case object SENDQ extends Code
  /**
   * Position parameter was wrong.
   */
  case object WRONG_POSITION extends Code

}

final case class ToxFileSendChunkException(code: ToxFileSendChunkException.Code, message: String = "")
  extends ToxException[ToxFileSendChunkException.Code](message)
