package im.tox.hlapi.storage

import java.io.IOException
import scala.util.Try
import scalaz._

sealed trait IOError

/** Denotes an invalid argument documented by the method that returned it. */
case object InvalidArgument extends IOError

/**
 * Denotes an attempt to use a slice invalidated by `file.unsafeResize`.
 *
 * If the slice was invalidated, then the size extended to a larger size,
 * whether this error is returned is implementation-defined.
 */
case object InvalidSlice extends IOError

/**
 * Default error, in case no error more relevant was found.
 *
 * In particular, this is returned when an exception
 * that is not an [[IOException]] was thrown.
 */
case object UnknownFailure extends IOError

/** Wraps an [[IOException]] as an [[IOError]] */
final case class Exception(exception: IOException) extends IOError

/** Helper functions to work with the `\/[IOError, _]` monad. */
object IOError {
  /**
   * Intercept exceptions and converts them to IOError.
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
   * @param condition under which the expression is evaluated.
   * @param error returned when [[condition]] is `false`.
   *        Defaults to [[InvalidArgument]].
   * @param value is the expression evaluated when [[condition]] is `true`.
   *
   * @return `-\/(error)` if the condition is falsified, `\/-(value)` otherwise.
   */
  def condition[A](condition: Boolean, error: IOError = InvalidArgument)(value: => A): \/[IOError, A] = {
    if (condition) {
      \/-(value)
    } else {
      -\/(error)
    }
  }

  /**
   * Conditionally evaluates a partial expression in the [[IOError]] monad.
   *
   * The expression may return errors or throw exceptions.
   * @param condition under which the expression is evaluated.
   * @param error returned when [[condition]] is `false`.
   *        Defaults to [[InvalidArgument]]
   * @param value is the expression evaluated when [[condition]] is `true`.
   *
   * @return `\/-(error)` if the condition is falsified, `value` if the
   *         evaluation didn't throw, and otherwise converts the
   *         exception to an [[IOError]].
   */
  def conditionWrap[A](condition: Boolean, error: IOError = InvalidArgument)(value: => \/[IOError, A]): \/[IOError, A] = {
    if (condition) {
      recover(Try(value))
    } else {
      -\/(error)
    }
  }
}
