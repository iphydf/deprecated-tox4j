package im.tox.tox4j.av

import im.tox.DiscreteIntCompanion

final class SamplingRate private (val value: Int) extends AnyVal

// scalastyle:off magic.number
object SamplingRate extends DiscreteIntCompanion[SamplingRate](
  8000, 12000, 16000, 24000, 48000
) {

  final val Rate8k = new SamplingRate(8000)
  final val Rate12k = new SamplingRate(12000)
  final val Rate16k = new SamplingRate(16000)
  final val Rate24k = new SamplingRate(24000)
  final val Rate48k = new SamplingRate(48000)

  override def unsafeFromInt(value: Int): SamplingRate = new SamplingRate(value)

}
