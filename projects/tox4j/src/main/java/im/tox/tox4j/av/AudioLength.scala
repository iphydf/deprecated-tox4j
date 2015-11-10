package im.tox.tox4j.av

final class AudioLength private (val value: Int) extends AnyVal

// scalastyle:off magic.number
object AudioLength extends IntWrapper[AudioLength] {
  final val Length2_5 = new AudioLength(2500)
  final val Length5 = new AudioLength(5000)
  final val Length10 = new AudioLength(10000)
  final val Length20 = new AudioLength(20000)
  final val Length40 = new AudioLength(40000)
  final val Length60 = new AudioLength(60000)

  override def unsafeFromInt(value: Int): AudioLength = new AudioLength(value)

  override def apply(value: Int): Option[AudioLength] = {
    value match {
      case 2500 | 5000 | 10000 | 20000 | 40000 | 60000 => Some(new AudioLength(value))
      case _ => None
    }
  }
}