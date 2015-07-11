package im.tox.tox4j.core.exceptions

import im.tox.tox4j.exceptions.ToxException

object ToxFileControlException {

  sealed trait Code extends ToxException.Code
  /**
   * A [[ToxFileControl.PAUSE]] control was sent, but the file transfer was already paused.
   */
  case object ALREADY_PAUSED extends Code
  /**
   * A [[ToxFileControl.RESUME]] control was sent, but the file transfer was paused by the other
   * party. Only the party that paused the transfer can resume it.
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
   * No file transfer with the given file number was found for the given friend.
   */
  case object NOT_FOUND extends Code
  /**
   * A [[ToxFileControl.RESUME]] control was sent, but the file transfer is running normally.
   */
  case object NOT_PAUSED extends Code
  /**
   * An allocation error occurred while increasing the send queue size.
   */
  case object SENDQ extends Code

}

final case class ToxFileControlException(code: ToxFileControlException.Code, message: String = "")
  extends ToxException[ToxFileControlException.Code](message)
