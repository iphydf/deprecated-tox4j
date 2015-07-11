package im.tox.tox4j.crypto.exceptions

import im.tox.tox4j.exceptions.ToxException

object ToxDecryptionException {

  sealed trait Code extends ToxException.Code
  /**
   * The input data is missing the magic number (i.e. wasn't created by this
   * module, or is corrupted)
   */
  case object BAD_FORMAT extends Code
  /**
   * The encrypted byte array could not be decrypted. Either the data was
   * corrupt or the password/key was incorrect.
   */
  case object FAILED extends Code
  /**
   * The input data was shorter than [[ToxCryptoConstants.ENCRYPTION_EXTRA_LENGTH]] bytes.
   */
  case object INVALID_LENGTH extends Code
  /**
   * The key or input data was null or empty.
   */
  case object NULL extends Code

}

final case class ToxDecryptionException(code: ToxDecryptionException.Code, message: String = "")
  extends ToxException[ToxDecryptionException.Code](message)
