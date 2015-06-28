package im.tox.tox4j.impl.tcp

import util.Random

import im.tox.tox4j.core.ToxCoreConstants

final class PublicKey private (val data: Array[Byte]) extends Serializable {
  override def equals(that: Any): Boolean = that match {
    case key: PublicKey => data.deep == key.data.deep
    case _              => false
  }

  override def hashCode: Int = data.hashCode

  override def toString: String = data.map(byte => f"$byte%02X").mkString
}

object PublicKey {
  val empty: PublicKey = new PublicKey(Array.ofDim[Byte](ToxCoreConstants.PUBLIC_KEY_SIZE))

  def apply(data: Array[Byte]): Option[PublicKey] = {
    if (data.length != ToxCoreConstants.PUBLIC_KEY_SIZE) {
      None
    } else {
      Some(new PublicKey(data))
    }
  }

  def random(): PublicKey = {
    val data = Array.ofDim[Byte](ToxCoreConstants.PUBLIC_KEY_SIZE)
    Random.nextBytes(data)
    new PublicKey(data)
  }
}
