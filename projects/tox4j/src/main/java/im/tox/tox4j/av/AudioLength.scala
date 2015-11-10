package im.tox.tox4j.av

import im.tox.core.typesafe.DiscreteIntCompanion

final class AudioLength private (val value: Int) extends AnyVal

// scalastyle:off magic.number
object AudioLength extends DiscreteIntCompanion[AudioLength](
  2500, 5000, 10000, 20000, 40000, 60000
) {

  val Length2_5 = new AudioLength(2500)
  val Length5 = new AudioLength(5000)
  val Length10 = new AudioLength(10000)
  val Length20 = new AudioLength(20000)
  val Length40 = new AudioLength(40000)
  val Length60 = new AudioLength(60000)

  override def unsafeFromInt(value: Int): AudioLength = new AudioLength(value)
  override def toInt(self: AudioLength): Int = self.value

}
