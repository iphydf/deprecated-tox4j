package im.tox

abstract class VariableSizeByteArrayCompanion[T <: AnyVal](val MaxSize: Int) extends (Array[Byte] => Option[T]) {

  def unsafeFromByteArray(value: Array[Byte]): T

  override def apply(value: Array[Byte]): Option[T] = {
    if (value.length <= MaxSize) {
      Some(unsafeFromByteArray(value))
    } else {
      None
    }
  }

}
