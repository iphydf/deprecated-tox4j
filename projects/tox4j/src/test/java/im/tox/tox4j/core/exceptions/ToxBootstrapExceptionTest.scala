package im.tox.tox4j.core.exceptions

import im.tox.tox4j.core.{Port, PublicKey, ToxCoreConstants}
import im.tox.tox4j.testing.ToxTestMixin
import org.scalatest.FunSuite

final class ToxBootstrapExceptionTest extends FunSuite with ToxTestMixin {

  test("BootstrapBadPort1") {
    interceptWithTox(ToxBootstrapException.Code.BAD_PORT)(
      _.bootstrap(
        "192.254.75.98",
        Port.unsafeFromInt(0),
        PublicKey.unsafeFromByteArray(new Array[Byte](ToxCoreConstants.PublicKeySize))
      )
    )
  }

  test("BootstrapBadPort2") {
    interceptWithTox(ToxBootstrapException.Code.BAD_PORT)(
      _.bootstrap(
        "192.254.75.98",
        Port.unsafeFromInt(-10),
        PublicKey.unsafeFromByteArray(new Array[Byte](ToxCoreConstants.PublicKeySize))
      )
    )
  }

  test("BootstrapBadPort3") {
    interceptWithTox(ToxBootstrapException.Code.BAD_PORT)(
      _.bootstrap(
        "192.254.75.98",
        Port.unsafeFromInt(65536),
        PublicKey.unsafeFromByteArray(new Array[Byte](ToxCoreConstants.PublicKeySize))
      )
    )
  }

  test("BootstrapBadHost") {
    interceptWithTox(ToxBootstrapException.Code.BAD_HOST)(
      _.bootstrap(
        ".",
        Port.unsafeFromInt(33445),
        PublicKey.unsafeFromByteArray(new Array[Byte](ToxCoreConstants.PublicKeySize))
      )
    )
  }

  test("BootstrapNullHost") {
    interceptWithTox(ToxBootstrapException.Code.NULL)(
      _.bootstrap(
        null,
        Port.unsafeFromInt(33445),
        PublicKey.unsafeFromByteArray(new Array[Byte](ToxCoreConstants.PublicKeySize))
      )
    )
  }

  test("BootstrapNullKey") {
    interceptWithTox(ToxBootstrapException.Code.NULL)(
      _.bootstrap(
        "localhost",
        Port.unsafeFromInt(33445),
        PublicKey.unsafeFromByteArray(null)
      )
    )
  }

  test("BootstrapKeyTooShort") {
    interceptWithTox(ToxBootstrapException.Code.BAD_KEY)(
      _.bootstrap(
        "localhost",
        Port.unsafeFromInt(33445),
        PublicKey.unsafeFromByteArray(new Array[Byte](ToxCoreConstants.PublicKeySize - 1))
      )
    )
  }

  test("BootstrapKeyTooLong") {
    interceptWithTox(ToxBootstrapException.Code.BAD_KEY)(
      _.bootstrap(
        "localhost",
        Port.unsafeFromInt(33445),
        PublicKey.unsafeFromByteArray(new Array[Byte](ToxCoreConstants.PublicKeySize + 1))
      )
    )
  }

}
