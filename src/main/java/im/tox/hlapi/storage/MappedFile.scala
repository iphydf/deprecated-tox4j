package im.tox.hlapi.storage

import scala.collection.immutable.Iterable
import scala.collection.GenTraversable

import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.nio.channels.FileChannel.MapMode
import java.io.{ File, RandomAccessFile }

object MappedFile {
  private val rw = "rw"

  /** Create a MappedFile, given a path, and truncate to the specified length */
  def apply(path: String, size: Int): MappedFile = {
    val file = new RandomAccessFile(path, rw)
    file.setLength(size * 1024)
    new MappedFile(file.getChannel())
  }

  /** Create a MappedFile from a path */
  def apply(path: String): MappedFile = {
    val file = new RandomAccessFile(path, rw)
    new MappedFile(file.getChannel())
  }
}

final class MappedFile(fileChannel: FileChannel) extends FileLike {
  override def size: Long = fileChannel.size

  override def apply(offset: Long, sliceSize: Int): Option[SliceImpl] =
    // TODO(nbraud) proper overflow checks
    if (sliceSize <= 0 || offset + sliceSize.toLong > size) {
      None
    } else {
      Some(new SliceImpl(
        fileChannel.map(MapMode.READ_WRITE, offset, sliceSize),
        sliceSize,
        offset
      ))
    }

  final class SliceImpl(
    private[MappedFile] val buffer: MappedByteBuffer,
    val size: Int,
    val offset: Long
  ) extends Slice

  override def flush(slice: SliceImpl): Unit = slice.buffer.force

  override def write(slice: SliceImpl)(offset: Int, data: Array[Byte]): Boolean = {
    if (offset >= 0 && offset + data.size <= slice.size) {
      // TODO(nbraud) Proper overflow checking
      for (i <- 0 until data.size) {
        slice.buffer.put(offset + i, data(i))
      }
      true
    } else {
      false
    }
  }

  override def get(slice: SliceImpl)(offset: Int): Option[Byte] = {
    if (offset < slice.size) {
      Some(slice.buffer.get(offset))
    } else {
      None
    }
  }

  override def set(slice: SliceImpl)(offset: Int, value: Byte): Boolean = {
    if (offset < slice.size) {
      slice.buffer.put(offset, value)
      true
    } else {
      false
    }
  }

  override def traverse(fileSlice: SliceImpl): GenTraversable[Byte] = {
    new Iterable[Byte] {
      def iterator = new SliceIterator(fileSlice)
    }
  }

  private class SliceIterator(slice: SliceImpl) extends Iterator[Byte] {
    private var index = 0 // scalastyle:ignore var.field
    def hasNext: Boolean = index < slice.size
    def next(): Byte = {
      val x = slice.buffer.get(index)
      if (hasNext) {
        index = index + 1
      }
      x
    }
  }

}
