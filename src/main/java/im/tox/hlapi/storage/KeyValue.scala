package im.tox.hlapi.storage

import scala.collection.GenTraversable

trait KeyValue[K <: KeyType, T <: ValueType[K]] extends GenTraversable[T] {
  // add should replace existing state with same id
  def add(obj: T)
  def lookup(id: K): Option[T]
  def delete(id: K): Boolean
  def delete(obj: T): Boolean = delete(obj.key)
}
