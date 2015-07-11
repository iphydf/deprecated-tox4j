package im.tox.tox4j.core.exceptions

import im.tox.tox4j.exceptions.ToxException

object ToxFileSeekException {

  sealed trait Code extends ToxException.Code
  /**
   * File was not in a state where it could be seeked.
   */
  case object DENIED extends Code
  /**
   * This client is currently not connected to the friend.
   */
  case object FRIEND_NOT_CONNECTED extends Code
  /**
   * The friendNumber passed did not designate a valid friend.
   */
  case object FRIEND_NOT_FOUND extends Code
  /**
   * Seek position was invalid.
   */
  case object INVALID_POSITION extends Code
  /**
   * No file transfer with the given file number was found for the given friend.
   */
  case object NOT_FOUND extends Code
  /**
   * An allocation error occurred while increasing the send queue size.
   */
  case object SENDQ extends Code

}

final case class ToxFileSeekException(code: ToxFileSeekException.Code, message: String = "")
  extends ToxException[ToxFileSeekException.Code](message)
