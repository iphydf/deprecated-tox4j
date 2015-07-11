package im.tox.tox4j.core.exceptions

import im.tox.tox4j.exceptions.ToxException

object ToxFileGetException {

  sealed trait Code extends ToxException.Code
  /**
   * The friendNumber passed did not designate a valid friend.
   */
  case object FRIEND_NOT_FOUND extends Code
  /**
   * No file transfer with the given file number was found for the given friend.
   */
  case object NOT_FOUND extends Code
  /**
   * An argument was null.
   */
  case object NULL extends Code

}

final case class ToxFileGetException(code: ToxFileGetException.Code, message: String = "")
  extends ToxException[ToxFileGetException.Code](message)
