package im.tox.tox4j.apidsl

import im.tox.annotations.constraint.RangeConstraint
import org.scalatest.FunSuite

final class NativeApiTest extends FunSuite {

  test("hello") {
    @NativeApi
    trait ToxCore[ToxCoreState] {
      @RangeConstraint(low = 0, high = 65535)
      private type Port = Int

      def getPort: Port
    }
  }

}
