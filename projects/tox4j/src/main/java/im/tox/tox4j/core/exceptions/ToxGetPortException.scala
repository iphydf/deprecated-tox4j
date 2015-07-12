package im.tox.tox4j.core.exceptions

import im.tox.tox4j.exceptions.ToxException

object ToxGetPortException {

  sealed trait Code extends ToxException.Code
  /**
   * The instance was not bound to any port.
   */
  case object NOT_BOUND extends Code

}

final case class ToxGetPortException(code: ToxGetPortException.Code, message: String = "")
  extends ToxException[ToxGetPortException.Code](message)
