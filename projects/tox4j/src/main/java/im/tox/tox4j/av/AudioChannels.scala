package im.tox.tox4j.av

import im.tox.DiscreteIntCompanion

final class AudioChannels private (val value: Int) extends AnyVal

object AudioChannels extends DiscreteIntCompanion[AudioChannels](1, 2) {

  final val Mono = new AudioChannels(1)
  final val Stereo = new AudioChannels(2)

  override def unsafeFromInt(value: Int): AudioChannels = new AudioChannels(value)

}
