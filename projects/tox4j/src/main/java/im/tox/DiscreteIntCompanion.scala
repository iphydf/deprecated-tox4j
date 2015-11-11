package im.tox

abstract class DiscreteIntCompanion[T <: AnyVal](values: Int*) extends (Int => Option[T]) {

  def unsafeFromInt(value: Int): T

  override def apply(value: Int): Option[T] = {
    if (values.contains(value)) {
      Some(unsafeFromInt(value))
    } else {
      None
    }
  }

}
