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

final object IOError {
  private def recover[A](tryValue: Try[\/[IOError, A]]): \/[IOError, A] = {
    tryValue
      .recover { case exn: IOException => -\/(Exception(exn)) }
      .getOrElse(-\/(UnknownFailure))
  }

  def wrap[A](value: => A): \/[IOError, A] = {
    recover(Try(value).map(\/-(_)))
  }

  def condition[A](cond: Boolean, alt: IOError = InvalidArgument)(value: => A): \/[IOError, A] = {
    if (cond) {
      \/-(value)
    } else {
      -\/(alt)
    }
  }

  def conditionWrap[A](cond: Boolean, alt: IOError = InvalidArgument)(value: => \/[IOError, A]): \/[IOError, A] = {
    if (cond) {
      recover(Try(value))
    } else {
      -\/(alt)
    }
  }
}
