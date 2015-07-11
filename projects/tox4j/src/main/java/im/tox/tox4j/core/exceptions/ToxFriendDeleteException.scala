package im.tox.tox4j.core.exceptions

import im.tox.tox4j.exceptions.ToxException

object ToxFriendDeleteException {

  sealed trait Code extends ToxException.Code
  /**
   * There was no friend with the given friend number. No friends were deleted.
   */
  case object FRIEND_NOT_FOUND extends Code

}

final case class ToxFriendDeleteException(code: ToxFriendDeleteException.Code, message: String = "")
  extends ToxException[ToxFriendDeleteException.Code](message)
