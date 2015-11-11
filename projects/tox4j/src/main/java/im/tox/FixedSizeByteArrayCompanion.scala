package im.tox

abstract class FixedSizeByteArrayCompanion[T <: AnyVal](val Size: Int) extends (Array[Byte] => Option[T]) {

  def unsafeFromByteArray(value: Array[Byte]): T

  override def apply(value: Array[Byte]): Option[T] = {
    if (value.length == Size) {
      Some(unsafeFromByteArray(value))
    } else {
      None
    }
  }

}
