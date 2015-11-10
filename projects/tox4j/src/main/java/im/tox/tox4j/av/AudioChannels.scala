package im.tox.tox4j.av

final class AudioChannels private (val value: Int) extends AnyVal

object AudioChannels extends IntWrapper[AudioChannels] {
  final val Mono = new AudioChannels(1)
  final val Stereo = new AudioChannels(2)

  override def unsafeFromInt(value: Int): AudioChannels = new AudioChannels(value)

  override def apply(value: Int): Option[AudioChannels] = {
    value match {
      case 1 | 2 => Some(new AudioChannels(value))
      case _     => None
    }
  }
}
