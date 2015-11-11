package im.tox.tox4j.core

import im.tox.BoundedIntCompanion

final class Port(val value: Int) extends AnyVal

object Port extends BoundedIntCompanion[Port](1, 0xffff) { // scalastyle:ignore magic.number
  override def unsafeFromInt(value: Int): Port = new Port(value)
}
