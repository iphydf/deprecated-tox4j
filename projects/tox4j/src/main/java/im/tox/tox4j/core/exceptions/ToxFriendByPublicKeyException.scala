package im.tox.tox4j.core.exceptions

import im.tox.tox4j.exceptions.ToxException

object ToxFriendByPublicKeyException {

  sealed trait Code extends ToxException.Code
  /**
   * No friend with the given Public Key exists on the friend list.
   */
  case object NOT_FOUND extends Code
  /**
   * An argument was null.
   */
  case object NULL extends Code

}

final case class ToxFriendByPublicKeyException(code: ToxFriendByPublicKeyException.Code, message: String = "")
  extends ToxException[ToxFriendByPublicKeyException.Code](message)
