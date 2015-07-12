package im.tox.tox4j.exceptions

import im.tox.tox4j.core.exceptions.ToxBootstrapException
import org.scalatest.FlatSpec
import org.scalatest.prop.PropertyChecks

final class ToxExceptionTest extends FlatSpec with PropertyChecks {

  val bootstrapExceptionCodes = Seq[ToxBootstrapException.Code](
    ToxBootstrapException.BAD_HOST,
    ToxBootstrapException.BAD_KEY,
    ToxBootstrapException.BAD_PORT,
    ToxBootstrapException.NULL
  )

  "getMessage" should "contain the error code name" in {
    bootstrapExceptionCodes.foreach { code =>
      val exn = ToxBootstrapException(code)
      assert(exn.getMessage.contains(code.toString))
    }
  }

  it should "contain the exception message" in {
    forAll { (message: String) =>
      val exn = ToxBootstrapException(ToxBootstrapException.NULL, message)
      assert(exn.getMessage.contains(message))
    }
  }

  "code" should "be the passed code" in {
    bootstrapExceptionCodes.foreach { code =>
      val exn = ToxBootstrapException(code)
      assert(exn.code == code)
    }
  }

}
