package im.tox.tox4j.av.exceptions

import im.tox.tox4j.exceptions.ToxException

object ToxAvNewException {

  sealed trait Code extends ToxException.Code
  /**
   * The ToxCore implementation passed was not compatible with this ToxAv implementation.
   */
  case object INCOMPATIBLE extends Code
  /**
   * Memory allocation failure while trying to allocate structures required for
   * the A/V session.
   */
  case object MALLOC extends Code
  /**
   * Attempted to create a second session for the same Tox instance.
   */
  case object MULTIPLE extends Code
  /**
   * One of the arguments to the function was NULL when it was not expected.
   */
  case object NULL extends Code

}

final case class ToxAvNewException(code: ToxAvNewException.Code, message: String = "")
  extends ToxException[ToxAvNewException.Code](message)
