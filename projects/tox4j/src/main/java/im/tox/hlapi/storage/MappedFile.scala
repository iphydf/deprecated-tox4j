package im.tox.hlapi.storage

import scala.collection.immutable.Iterable
import scala.collection.GenTraversable
import scalaz._
import scalaz.syntax.either._

import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.nio.channels.FileChannel.MapMode
import java.io.{ File, IOException, RandomAccessFile }

object MappedFile {
  private val rw = "rw"

  /**
   * Creates a MappedFile, given a path, and truncate to the specified length.
   *
   * @param The file path, expressed as a [[String]].
   * @param The new size in bytes, expressed as a [[Long]].
   * It must be positive (incl. zero).
   */
  def apply(path: String, size: Long): \/[IOError, MappedFile] = {
    IOError.wrap {
      val file = new RandomAccessFile(path, rw)
      file.setLength(size)
      new MappedFile(file)
    }
  }

  /**
   * Creates a MappedFile from a path.
   *
   * @param The file path, expressed as a [[String]].
   */
  def apply(path: String): \/[IOError, MappedFile] = {
    IOError.wrap {
      val file = new RandomAccessFile(path, rw)
      new MappedFile(file)
    }
  }
}

final class MappedFile(file: RandomAccessFile) extends FileLike {
  override def size: Long = file.length

  override def slice(offset: Long, size: Int): \/[IOError, Slice] = {
    // TODO(nbraud) proper overflow checks
    IOError.condition(size >= 0 && offset >= 0L && offset + size <= this.size) {
      new Slice(
        file.getChannel.map(MapMode.READ_WRITE, offset, size),
        offset,
        size
      )
    }
  }

  final class Slice private[MappedFile] (
    private[MappedFile] val buffer: MappedByteBuffer,
    offset: Long,
    size: Int
  ) extends SliceBase(offset, size)

  override def flush(slice: Slice): \/[IOError, Unit] = {
    IOError.wrap { slice.buffer.force }
  }

  override def writeSeq(slice: Slice)(offset: Int, data: Seq[Byte]): \/[IOError, Unit] = {
    // TODO(nbraud) Proper overflow checking
    IOError.conditionWrap(offset >= 0 && offset + data.size <= slice.size) {
      IOError.condition(slice.offset + slice.size <= size, InvalidSlice) {
        data.zipWithIndex.foreach {
          case (byte, i) => slice.buffer.put(offset + i, byte)
        }
      }
    }
  }

  override def readByte(slice: Slice)(offset: Int): \/[IOError, Byte] = {
    IOError.conditionWrap(offset < slice.size && offset >= 0) {
      IOError.condition(slice.offset + slice.size <= size, InvalidSlice) {
        slice.buffer.get(offset)
      }
    }
  }

  override def writeByte(slice: Slice)(offset: Int, value: Byte): \/[IOError, Unit] = {
    IOError.conditionWrap(offset < slice.size && offset >= 0) {
      IOError.condition(slice.offset + slice.size <= size, InvalidSlice) {
        slice.buffer.put(offset, value)
        ()
      }
    }
  }

  override def readSeq(slice: Slice): \/[IOError, GenTraversable[Byte]] = {
    IOError.condition(slice.offset + slice.size <= size, InvalidSlice) {
      val fileSlice = slice // Required to avoid name shadowing
      new Iterable[Byte] {
        override def iterator = new SliceIterator(fileSlice)
        override def size = fileSlice.size
      }
    }
  }

  private class SliceIterator(slice: Slice) extends Iterator[Byte] {
    private var index = 0 // scalastyle:ignore var.field
    override def hasNext: Boolean = index < slice.size
    override def next: Byte = {
      val x = slice.buffer.get(index)
      if (hasNext) {
        index = index + 1
      }
      x
    }
  }

  override def unsafeTruncate(size: Long): \/[IOError, Unit] = {
    IOError
      .wrap { file.setLength(size): Unit }
      .flatMap { _ => IOError.condition(this.size == size, UnknownFailure) {} }
  }
}
