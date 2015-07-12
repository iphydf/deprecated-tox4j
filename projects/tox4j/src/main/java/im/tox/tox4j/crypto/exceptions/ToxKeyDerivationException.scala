package im.tox.tox4j.crypto.exceptions

import im.tox.tox4j.exceptions.ToxException

object ToxKeyDerivationException {

  sealed trait Code extends ToxException.Code
  /**
   * The crypto lib was unable to derive a key from the given passphrase,
   * which is usually a lack of memory issue. The functions accepting keys
   * do not produce this error.
   */
  case object FAILED extends Code
  /**
   * The salt was of incorrect length.
   */
  case object INVALID_LENGTH extends Code
  /**
   * The passphrase was null or empty.
   */
  case object NULL extends Code

}

final case class ToxKeyDerivationException(code: ToxKeyDerivationException.Code, message: String = "")
  extends ToxException[ToxKeyDerivationException.Code](message)
