package im.tox.tox4j.av

import im.tox.BoundedIntCompanion

final class BitRate private (val value: Int) extends AnyVal

object BitRate extends BoundedIntCompanion[BitRate](-1, Int.MaxValue) {

  final val Unchanged = new BitRate(-1)
  final val Disabled = new BitRate(0)

  override def unsafeFromInt(value: Int): BitRate = new BitRate(value)

}
