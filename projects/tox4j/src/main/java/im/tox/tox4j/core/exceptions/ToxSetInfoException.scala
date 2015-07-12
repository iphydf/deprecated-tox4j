package im.tox.tox4j.core.exceptions

import im.tox.tox4j.exceptions.ToxException

object ToxSetInfoException {

  sealed trait Code extends ToxException.Code
  /**
   * An argument was null.
   */
  case object NULL extends Code
  /**
   * Information length exceeded maximum permissible size.
   */
  case object TOO_LONG extends Code

}

final case class ToxSetInfoException(code: ToxSetInfoException.Code, message: String = "")
  extends ToxException[ToxSetInfoException.Code](message)
