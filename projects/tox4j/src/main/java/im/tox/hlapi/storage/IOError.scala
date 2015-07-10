package im.tox.hlapi.storage

import java.io.IOException
import scala.util.Try
import scalaz._

sealed trait IOError

final case object InvalidArgument extends IOError

/**
 * Denotes an attempt to use a slice invalidated by `file.unsafeResize`.
 *
 * If the slice was invalidated, then the size extended to a larger size,
 * whether this error is returned is implementation-defined.
 */
final case object InvalidSlice extends IOError

final case object UnknownFailure extends IOError

final case class Exception(exn: IOException) extends IOError

/** Helper functions to work with the `\/[IOError, _]` monad. */
final object IOError {
  /** Intercept exceptions and converts them to IOError.
   *
   * @returns `-\/(Exception(_))` if an [[IOException]] is intercepted,
   *  else returns `-\/(UnknownFailure)` when intercepting another exception.
   */
  private def recover[A](tryValue: Try[\/[IOError, A]]): \/[IOError, A] = {
    tryValue
      .recover { case exn: IOException => -\/(Exception(exn)) }
      .getOrElse(-\/(UnknownFailure))
  }

  /** Wraps an exception-raising expression into the [[IOError]] monad. */
  def wrap[A](value: => A): \/[IOError, A] = {
    recover(Try(value).map(\/-(_)))
  }

  /**
   * Conditionally evaluates a total expression in the [[IOError]] monad.
   *
   * The expression must not throw exceptions.
   *
   * @param The condition under which the expression is evaluated.
   * @param The [[IOError]] returned when [[condition]] is `false`.
   * Defaults to [[InvalidArgument]]
   * @param The expression which is evaluated when [[condition]] is `true`.
   *
   * @return `-\/(alt)` if the condition is falsified, `\/-(value)` otherwise.
   */
  def condition[A](condition: Boolean, alt: IOError = InvalidArgument)(value: => A): \/[IOError, A] = {
    if (cond) {
      \/-(value)
    } else {
      -\/(alt)
    }
  }

  /**
   * Conditionally evaluates a partial expression in the [[IOError]] monad.
   *
   * The expression may return errors or throw exceptions.
   * @param The condition under which the expression is evaluated.
   * @param The [[IOError]] returned when [[condition]] is `false`.
   * Defaults to [[InvalidArgument]]
   * @param The expression which is evaluated when [[condition]] is `true`.
   *
   * @return `\/-(alt)` if the condition is falsified, `value` if the evaluation
   * didn't throw, and otherwise converts the exception to an [[IOError]].
   */
  def conditionWrap[A](cond: Boolean, alt: IOError = InvalidArgument)(value: => \/[IOError, A]): \/[IOError, A] = {
    if (cond) {
      recover(Try(value))
    } else {
      -\/(alt)
    }
  }
}
