package im.tox.hlapi.storage

import org.scalacheck._
import org.scalacheck.commands._
import org.scalacheck.Arbitrary._

import org.scalatest._
import org.scalatest.prop.PropertyChecks

private object MappedFileSpec extends FileLikeSpec {
  type Sut = MappedFile

  def createFile(size: Long): Sut = TempMappedFile(size)
}

final class MappedFileTest extends FlatSpec {
  "MappedFile" should "be a proper FileLike implementation" in {
    MappedFileSpec.property().check
  }

  "MappedFile" should "prevent mixing slices from different files" in {
    val slice = TempMappedFile(1024).slice(0L, 150).toOption.get
    val file = TempMappedFile(1024)
    assertTypeError("file.readByte(slice)")
    assertTypeError("file.writeByte(slice)")
    assertTypeError("file.read(slice)")
    assertTypeError("file.writeSeq(slice)")
    assertTypeError("file.flush(slice)")
  }

  // TODO(nbraud): Test truncate & flush
}
