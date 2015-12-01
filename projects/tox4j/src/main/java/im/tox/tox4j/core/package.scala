package im.tox.tox4j

import im.tox.documentation.Documented

package object core extends Documented {

  /**
   * Ignore the error type and return the result type directly. The exception containing the error
   * needs to be caught.
   */
  type IdentityResult[E <: Enum[E], A] = A

}
