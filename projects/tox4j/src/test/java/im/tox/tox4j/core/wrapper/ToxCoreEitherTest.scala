package im.tox.tox4j.core.wrapper

import im.tox.tox4j.core.ToxCoreFactory
import im.tox.tox4j.core.data.ToxFriendMessage
import im.tox.tox4j.core.enums.ToxMessageType
import im.tox.tox4j.core.exceptions.ToxFriendSendMessageException
import im.tox.tox4j.testing.GetDisjunction._
import org.scalatest.FunSuite

final class ToxCoreEitherTest extends FunSuite {

  test("error recovery with Either") {
    ToxCoreFactory.withTox { tox =>
      val toxWrap = new ToxCoreEither(tox)
      val result = toxWrap.friendSendMessage(0, ToxMessageType.NORMAL, 0, ToxFriendMessage.fromString("hello").get)
      assert(result == Left(ToxFriendSendMessageException.Code.FRIEND_NOT_FOUND))
    }
  }

}
