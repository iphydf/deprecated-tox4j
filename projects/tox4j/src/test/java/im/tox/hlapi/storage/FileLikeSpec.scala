package im.tox.hlapi.storage

import scala.collection.immutable.{ HashMap, Map }
import scala.collection.GenTraversable
import scalaz._

import org.scalacheck._
import org.scalacheck.commands._
import org.scalacheck.Arbitrary._

import org.scalatest._
import org.scalatest.prop.PropertyChecks

trait FileLikeSpec extends Commands {

  type Sut <: FileLike
  def createFile(size: Long): Sut

  final case class State(size: Long, values: Map[Long, Byte]) {
    def get(position: Long): Option[Byte] = values.get(position)
    def set(position: Long, value: Byte): State = {
      copy(values = values + ((position, value)))
    }
  }

  private val maxInitialSize = 1024L * 1024L

  final override def genInitialState: Gen[State] = {
    for {
      offsets <- Gen.nonEmptyContainerOf[List, Long](Gen.choose(0L, maxInitialSize))
      values <- Gen.containerOfN[List, Byte](offsets.size, arbitrary[Byte])
      size <- Gen.choose(
        1L + (10L max offsets.max),
        2L * (10L max offsets.max)
      )
    } yield State(size, HashMap(offsets.zip(values): _*))
  }

  override def initialPreCondition(s: State): Boolean = true
  override def canCreateNewSut(newState: State, initSuts: Traversable[State],
    runningSuts: Traversable[Sut]): Boolean = true

  final override def newSut(state: State): Sut = {
    assert(state.size.isValidInt)
    val file = createFile(state.size)
    val slice = file.slice(0, state.size.toInt).toOption.get

    state.values.foreach {
      case (position, byte) =>
        assert(position >= 0 && position < state.size)
        file.writeByte(slice)(position.toInt, byte)
    }
    file
  }

  override def destroySut(file: Sut): Unit = ()

  /* TODO(nbraud) Check that any sequence of read/write to any number of
   * (potentially overlapping) slices (in a single thread, over the same
   * FileLike) produce the same trace as the equivalent read/write sequence
   * on the model.
   */

  private final case class Get(position: Long) extends SuccessCommand {
    type Result = \/[IOError, Byte]
    def nextState(state: State): State = state

    def preCondition(state: State): Boolean = {
      assert(position >= 0L && position < state.size)
      true
    }

    def postCondition(state: State, result: Result): Prop = {
      result match {
        case -\/(_) => Prop(false)
        case \/-(x) =>
          Prop(state.get(position).forall(_ == x))
      }
    }

    def run(file: Sut): Result = {
      val slice = file.slice(position, 1).toOption.get
      file.readByte(slice)(0)
    }
  }

  private final case class Set(position: Long, value: Byte) extends SuccessCommand {
    type Result = \/[IOError, Unit]
    def nextState(state: State): State = {
      state.set(position, value)
    }

    def preCondition(state: State): Boolean = {
      assert(position >= 0L && position < state.size)
      true
    }

    def postCondition(state: State, result: Result): Prop = Prop(result.isRight)

    def run(file: Sut): Result = {
      val slice = file.slice(position, 1).toOption.get
      file.writeByte(slice)(0, value)
    }

  }

  private final case class Read(position: Long, size: Int) extends SuccessCommand {
    type Result = \/[IOError, GenTraversable[Byte]]
    def nextState(state: State): State = state

    def preCondition(state: State): Boolean = {
      assert(position >= 0L)
      assert(size >= 0)
      assert(position + size <= state.size)
      true
    }

    def postCondition(state: State, result: Result): Prop = {
      result.map {
        _.toIterable.zipWithIndex.forall {
          case (byte, i) =>
            state.get(position + i) match {
              case None       => true
              case Some(data) => data == byte
            }
        }
      }.map(Prop(_)).getOrElse(Prop(false))
    }

    def run(file: Sut): Result = {
      val slice = file.slice(position, size).toOption.get
      file.readSeq(slice)
    }
  }

  private final case class Write(position: Long, size: Int, data: Seq[Byte])
      extends SuccessCommand {
    type Result = Unit

    def nextState(state: State): State = {
      data
        .zipWithIndex
        .foldLeft(state) { case (s, (byte, i)) => s.set(position + i, byte) }
    }

    def preCondition(state: State): Boolean = {
      assert(size >= 0 && position >= 0 && position + size <= state.size)
      true
    }

    def postCondition(state: State, result: Result): Prop = Prop(true)

    def run(file: Sut): Result = {
      val slice = file.slice(position, size).toOption.get
      file.writeSeq(slice)(0, data)
    }
  }

  private final case class Overlap(
      position: Long,
      length1: Int, overlap: Int, length2: Int
  ) extends SuccessCommand {
    type Result = (Byte, Byte, Byte, Byte)
    def nextState(state: State): State = {
      state
        .set(position + length1 - overlap, 0xCA.toByte)
        .set(position + length1 - overlap + 1, 0xFE.toByte)
    }

    def preCondition(state: State): Boolean = {
      assert(
        position >= 0L && position + length1 + length2 <= state.size + overlap &&
          length1 >= 0 && length1 >= overlap &&
          length2 >= 0 && length2 >= overlap &&
          overlap >= 2
      )
      true
    }

    def postCondition(state: State, result: Result): Prop = {
      Prop(result == ((0xCA.toByte, 0xFE.toByte, 0xCA.toByte, 0xFE.toByte)))
    }

    def run(file: Sut): Result = {
      val slice1 = file.slice(position, length1).toOption.get
      val slice2 = file.slice(position + length1 - overlap, length2).toOption.get
      assert(file.writeByte(slice1)(length1 - overlap, 0xCA.toByte).isRight)
      assert(file.writeByte(slice2)(1, 0xFE.toByte).isRight)

      (
        file.readByte(slice1)(length1 - overlap).getOrElse(0xDE.toByte),
        file.readByte(slice1)(length1 - overlap + 1).getOrElse(0xAD.toByte),
        file.readByte(slice2)(0).getOrElse(0xBE.toByte),
        file.readByte(slice2)(1).getOrElse(0xBE.toByte)
      )
    }
  }

  protected final def truncateToInt(x: Long): Int = {
    assert(x >= 0)
    (x min Int.MaxValue).toInt
  }

  private def genGet(state: State): Gen[Get] = {
    Gen.choose(0L, state.size - 1).map(Get(_))
  }

  private def genSet(state: State): Gen[Set] = {
    for {
      position <- Gen.choose(0L, state.size - 1)
      value <- arbitrary[Byte]
    } yield Set(position, value)
  }

  private def genOverlap(state: State): Gen[Overlap] = {
    val size = state.size
    for {
      position <- Gen.choose(0L, size - 10L)
      /* Overlap generates a pair of slices, of sizes `length1`
       * and `length2`, which overlap on `overlap` bytes.
       */
      length1 <- Gen.choose(2, truncateToInt(size - position - 8L))
      overlap <- Gen.choose(2, truncateToInt(size - position - 5L) min length1)
      length2 <- Gen.choose(overlap, truncateToInt(size - position - length1 + overlap))
    } yield Overlap(position, length1, overlap, length2)
  }

  protected final def genPositionLength(state: State): Gen[(Long, Int)] = {
    for {
      position <- Gen.choose(0L, state.size - 2L)
      length <- Gen.choose(1, truncateToInt(state.size - position) min 1024)
    } yield (position, length)
  }

  private def genRead(state: State): Gen[Read] = {
    for {
      (position, length) <- genPositionLength(state)
    } yield Read(position, length)
  }

  private def genWrite(state: State): Gen[Write] = {
    for {
      (position, length) <- genPositionLength(state)
      data <- Gen.containerOfN[Seq, Byte](length, arbitrary[Byte])
    } yield Write(position, length, data)
  }

  override def genCommand(state: State): Gen[Command] = {
    Gen.oneOf[Command](
      genGet(state),
      genSet(state),
      genRead(state),
      genWrite(state),
      genOverlap(state)
    )
  }

}
