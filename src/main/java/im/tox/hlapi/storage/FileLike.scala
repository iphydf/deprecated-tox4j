package im.tox.hlapi.storage

import scala.collection.GenTraversable

trait FileLike {
  // TODO(nbraud) Need proper error monad
  def apply(offset: Long, sliceSize: Int): Option[Slice]
  def size: Long

  type SliceImpl <: Slice

  abstract class Slice {
    val offset: Long
    val size: Int
  }

  // All write operations performed before a flush() MUST be written to
  //  persistent storage. They MAY be shadowed by later set() to the same
  //  location.
  def flush(slice: SliceImpl): Unit

  def write(slice: SliceImpl)(offset: Int, data: Array[Byte]): Boolean

  def get(slice: SliceImpl)(offset: Int): Option[Byte]
  def set(slice: SliceImpl)(offset: Int, value: Byte): Boolean

  def traverse(slice: SliceImpl): GenTraversable[Byte]
}
