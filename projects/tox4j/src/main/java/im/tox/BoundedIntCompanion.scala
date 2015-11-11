package im.tox

abstract class BoundedIntCompanion[T <: AnyVal](val MinValue: Int, val MaxValue: Int) extends (Int => Option[T]) {

  def unsafeFromInt(value: Int): T

  final override def apply(value: Int): Option[T] = {
    if (MinValue <= value && value <= MaxValue) {
      Some(unsafeFromInt(value))
    } else {
      None
    }
  }

}
