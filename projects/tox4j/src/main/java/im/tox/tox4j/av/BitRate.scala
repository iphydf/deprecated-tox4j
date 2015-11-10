package im.tox.tox4j.av

final class BitRate private (val value: Int) extends AnyVal

object BitRate extends IntWrapper[BitRate] {
  final val Unchanged = new BitRate(-1)
  final val Disabled = new BitRate(0)

  override def unsafeFromInt(value: Int): BitRate = new BitRate(value)

  override def apply(value: Int): Option[BitRate] = {
    if (value >= Unchanged.value) {
      Some(new BitRate(value))
    } else {
      None
    }
  }
}
