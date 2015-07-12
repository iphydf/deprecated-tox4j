package im.tox.tox4j.core.exceptions

import im.tox.tox4j.exceptions.ToxException

object ToxFriendGetPublicKeyException {

  sealed trait Code extends ToxException.Code
  /**
   * The friendNumber passed did not designate a valid friend.
   */
  case object FRIEND_NOT_FOUND extends Code

}

final case class ToxFriendGetPublicKeyException(code: ToxFriendGetPublicKeyException.Code, message: String = "")
  extends ToxException[ToxFriendGetPublicKeyException.Code](message)
