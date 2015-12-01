package im.tox.tox4j.core.wrapper

import im.tox.tox4j.core._
import im.tox.tox4j.core.exceptions._
import im.tox.tox4j.core.options.ToxOptions

import scalaz.{\/-, -\/, \/}

final class ToxCoreDisjunction[ToxCoreState](tox: ToxCore[ToxCoreState])
    extends ToxCoreWrapper[ToxCoreState, DisjunctionResult, ToxCoreDisjunction[ToxCoreState]](tox) {

  protected override def toLeft[A](a: A): A \/ Nothing = -\/(a)
  protected override def toRight[B](b: B): Nothing \/ B = \/-(b)

  override def load(options: ToxOptions): ToxNewException.Code \/ ToxCoreDisjunction[ToxCoreState] = {
    catchErrorCode[ToxCoreDisjunction[ToxCoreState], ToxNewException.Code, ToxNewException](new ToxCoreDisjunction(tox.load(options)))
  }

}
