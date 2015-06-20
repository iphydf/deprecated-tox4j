package im.tox.hlapi.storage

trait ValueType[K] extends Serializable {
  val key: K
}
