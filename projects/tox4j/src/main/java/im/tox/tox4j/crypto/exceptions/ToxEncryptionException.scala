package im.tox.tox4j.crypto.exceptions

import im.tox.tox4j.exceptions.ToxException

object ToxEncryptionException {

  sealed trait Code extends ToxException.Code
  /**
   * The encryption itself failed.
   */
  case object FAILED extends Code
  /**
   * The key or input data was null or empty.
   */
  case object NULL extends Code

}

final case class ToxEncryptionException(code: ToxEncryptionException.Code, message: String = "")
  extends ToxException[ToxEncryptionException.Code](message)
