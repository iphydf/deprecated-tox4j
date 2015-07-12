package im.tox.tox4j.core.exceptions

import im.tox.tox4j.exceptions.ToxException

object ToxNewException {

  sealed trait Code extends ToxException.Code
  /**
   * The data format was invalid. This can happen when loading data that was
   * saved by an older version of Tox, or when the data has been corrupted.
   * When loading from badly formatted data, some data may have been loaded,
   * and the rest is discarded. Passing an invalid length parameter also
   * causes this error.
   */
  case object LOAD_BAD_FORMAT extends Code
  /**
   * An encrypted save format was found, but the key was wrong or the data was corrupt.
   */
  case object LOAD_DECRYPTION_FAILED extends Code
  /**
   * The byte array to be loaded contained an encrypted save.
   */
  case object LOAD_ENCRYPTED extends Code
  /**
   * The function was unable to allocate enough memory to store the internal
   * structures for the Tox object.
   */
  case object MALLOC extends Code
  /**
   * An argument was null.
   */
  case object NULL extends Code
  /**
   * The function was unable to bind to a port. This may mean that all ports
   * have already been bound, e.g. by other Tox instances, or it may mean
   * a permission error. You may be able to gather more information from errno.
   */
  case object PORT_ALLOC extends Code
  /**
   * [[ToxOptions.proxyType]] was valid,
   * but the [[ToxOptions.proxyAddress]] passed had an invalid format.
   */
  case object PROXY_BAD_HOST extends Code
  /**
   * [[ToxOptions.proxyType]] was valid,
   * but the [[ToxOptions.proxyPort]] was invalid.
   */
  case object PROXY_BAD_PORT extends Code
  /**
   * [[ToxOptions.proxyType]] was invalid.
   */
  case object PROXY_BAD_TYPE extends Code
  /**
   * The proxy address passed could not be resolved.
   */
  case object PROXY_NOT_FOUND extends Code

}

final case class ToxNewException(code: ToxNewException.Code, message: String = "")
  extends ToxException[ToxNewException.Code](message)
