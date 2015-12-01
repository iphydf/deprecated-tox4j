package im.tox.tox4j.core

import scalaz.\/

package object wrapper {

  /**
   * Wrap the error code in a [[\/]].
   */
  type DisjunctionResult[+E <: Enum[E], +A] = E \/ A

  /**
   * Wrap the error code in a [[\/]].
   */
  type EitherResult[+E <: Enum[E], +A] = Either[E, A]

}
