package im.tox.hlapi.storage

import scala.collection.immutable.{ Map, Set }

object KeyValueWrapper {
  def apply[K <: KeyType, T <: ValueType[K]](
    storage: KeyValue[K, T]
  ): KeyValueWrapper[K, T] = {
    KeyValueWrapper(storage, Map.empty, Set.empty)
  }
}

private[hlapi] final case class KeyValueWrapper[K <: KeyType, T <: ValueType[K]](
    storage: KeyValue[K, T],
    pendingAdd: Map[K, T],
    pendingDel: Set[K]
) {
  // TODO(nbraud) Make GenTraversable (or finer type)

  def add(obj: T): KeyValueWrapper[K, T] = {
    this.copy(
      pendingAdd = pendingAdd + ((obj.key, obj)),
      pendingDel = pendingDel - obj.key
    )
  }

  def lookup(id: K): Option[T] = {
    if (pendingDel(id)) {
      None
    } else {
      pendingAdd.get(id) match {
        case Some(x) => Some(x)
        case None    => storage.lookup(id)
      }
    }
  }

  def delete(id: K): (KeyValueWrapper[K, T], Boolean) = {
    if (pendingDel(id)) {
      (this, false)
    } else if (pendingAdd.contains(id)) {
      (this.copy(
        pendingAdd = pendingAdd - id,
        pendingDel = pendingDel + id
      ), true)
    } else if (storage.lookup(id) == None) {
      (this, false)
    } else {
      (this.copy(pendingDel = pendingDel + id), false)
    }
  }

  def delete(obj: T): (KeyValueWrapper[K, T], Boolean) = {
    delete(obj.key)
  }

  private[hlapi] def performIO(): KeyValueWrapper[K, T] = {
    pendingAdd.values.foreach(storage.add);
    pendingDel.foreach(storage.delete);
    this.copy(pendingAdd = Map.empty, pendingDel = Set.empty)
  }
}
