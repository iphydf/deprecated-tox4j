package im.tox.hlapi.storage

import scala.collection.{ Seq, GenTraversable }
import scalaz._

trait FileLike {
  /**
   * [[SliceBase]] is the interface all [[Slice]] types must implement.
   *
   * It contains immutable `offset` and `size` fields, which describe the range
   * that the slice spans within the file.
   */
  protected abstract class SliceBase(
    final val offset: Long,
    final val size: Int
  )

  /**
   * The type of [[Slice]]s associated with this [[FileLike]]
   *
   * Slices represent a contiguous part of a [[FileLike]], and are
   * handles used for reading from and writing to files.
   * [[Slice]] is a path-dependent type, which ensures that a given [[Slice]]
   * cannnot be used with a [[FileLike]] it didn't originate from.
   * Their size must be expressible as a signed [[Int]], as such it
   * cannot exceed 2GB.
   */
  type Slice <: SliceBase

  /**
   * The file's size, in bytes.
   */
  def size: Long

  /**
   * Resizes the file. Can be used for truncation or for expanding a file.
   *
   * Must be called with a non-negative argument.
   * If a file is truncated in a way which invalidates currently mapped slices,
   * they will systematically return an [[IOError]] on operations. If the file
   * is later extended in such a way that a slice is valid again, is may
   * be subjected to the usual semantic, or it may error out on any operation.
   */
  def unsafeResize(size: Long): \/[IOError, Unit]

  /**
   * Extends the file.
   *
   * Must be called with a `size` greater or equal to `this.size`.
   */
  def extend(size: Long): \/[IOError, Unit] = {
    IOError.condition(size >= this.size) { unsafeResize(size) }
  }

  /**
   * Obtain a [[Slice]] that represents part of the file.
   *
   * `offset` and `size` must be positive, and
   * `offset + size <= this.size`.
   *
   * @returns `-\/(InvalidArgument)` if that constraint is not respected.
   * It may return any other [[IOError]] or, on success, a valid [[Slice]].
   */
  def slice(offset: Long, size: Int): \/[IOError, Slice]

  /**
   * Writes pending changes to persistent storage.
   *
   * All write operations performed before a [[flush]] are written to persistent
   * storage. They may be shadowed by later writes to overlapping locations.
   */
  def flush(slice: Slice): \/[IOError, Unit]

  /**
   * Writes multiple bytes, given as an ordered sequence.
   *
   * @return `-\/(InvalidArgument)` if the write would cause an out-of-bound
   * write (`offset + data.size > slice.size` or `offset < 0`).
   */
  def writeSeq(slice: Slice)(offset: Int, data: Seq[Byte]): \/[IOError, Unit]

  /**
   * Reads the whole slice.
   *
   * The slice is traversed lazily.
   * Concurrent writes to it (or overlapping slices) may appear in the read.
   *
   * The read may fail if the slice was invalidated by [[unsafeResize]].
   */
  def readSeq(slice: Slice): \/[IOError, GenTraversable[Byte]]

  /**
   * Writes a single [[Byte]] to the slice.
   *
   * @return `-\/(InvalidArgument)` if the write would be out-of-bounds.
   * (`offset >= slice.size` or `offset < 0`).
   */
  def writeByte(slice: Slice)(offset: Int, value: Byte): \/[IOError, Unit]

  /**
   * Reads a single [[Byte]] from the slice.
   *
   * @return `-\/(InvalidArgument)` if the read would be
   * out-of-bounds (`offset >= slice.size` or `offset < 0`).
   */
  def readByte(slice: Slice)(offset: Int): \/[IOError, Byte]
}
