package im.tox.hlapi.storage

import java.io.IOException
import scala.util.Try
import scalaz._

import org.scalacheck._
import org.scalacheck.Arbitrary._

import org.scalatest._
import org.scalatest.prop.GeneratorDrivenPropertyChecks

final class IOErrorTest extends GeneratorDrivenPropertyChecks with ShouldMatchers {
  final val genError: Gen[IOError] = Gen.oneOf(InvalidArgument, UnknownFailure)
  final val genException: Gen[IOException] = {
    val reason = "Produced in IOErrorTest"
    Gen.oneOf(
      new java.io.InterruptedIOException(),
      new java.io.SyncFailedException(reason),
      new java.nio.file.FileSystemException(null, null, reason)
    )
  }

  forAll(genError) { (error: IOError) =>
    IOError.condition(true, error) {} should equal(\/-(()))
    IOError.condition(false, error) {} should equal(-\/(error))

    IOError.conditionWrap(true) { -\/(error) } should equal(-\/(error))
    IOError.conditionWrap(true, error) { \/-(()) } should equal(-\/(()))
    IOError.conditionWrap(false, error) { \/-(()) } should equal(-\/(error))
  }

  forAll(genException) { (exn: IOException) =>
    IOError.wrap { throw exn } should equal(-\/(Exception(exn)))
    IOError.conditionWrap(true) { throw exn } should equal(-\/(Exception(exn)))
  }

  IOError.wrap { throw new RuntimeException("foo") } should equal(-\/(UnknownFailure))
}
