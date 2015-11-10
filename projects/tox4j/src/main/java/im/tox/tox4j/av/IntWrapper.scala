package im.tox.tox4j.av

abstract class IntWrapper[T] extends (Int => Option[T]) {
  def unsafeFromInt(value: Int): T
}
