package im.tox.tox4j.core.wrapper

import im.tox.tox4j.core._
import im.tox.tox4j.core.exceptions._
import im.tox.tox4j.core.options.ToxOptions

final class ToxCoreEither[ToxCoreState](tox: ToxCore[ToxCoreState])
    extends ToxCoreWrapper[ToxCoreState, EitherResult, ToxCoreEither[ToxCoreState]](tox) {

  protected override def toLeft[A](a: A): Either[A, Nothing] = Left(a)
  protected override def toRight[B](b: B): Either[Nothing, B] = Right(b)

  override def load(options: ToxOptions): Either[ToxNewException.Code, ToxCoreEither[ToxCoreState]] = {
    catchErrorCode[ToxCoreEither[ToxCoreState], ToxNewException.Code, ToxNewException](new ToxCoreEither(tox.load(options)))
  }

}
