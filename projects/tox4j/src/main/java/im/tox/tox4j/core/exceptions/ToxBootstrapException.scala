package im.tox.tox4j.core.exceptions

import im.tox.tox4j.exceptions.ToxException

object ToxBootstrapException {

  sealed trait Code extends ToxException.Code
  /**
   * The address could not be resolved to an IP address, or the IP address
   * passed was invalid.
   */
  case object BAD_HOST extends Code
  /**
   * The public key was of invalid length.
   */
  case object BAD_KEY extends Code
  /**
   * The port passed was invalid. The valid port range is (1, 65535).
   */
  case object BAD_PORT extends Code
  /**
   * An argument was null.
   */
  case object NULL extends Code

}

final case class ToxBootstrapException(code: ToxBootstrapException.Code, message: String = "")
  extends ToxException[ToxBootstrapException.Code](message)
