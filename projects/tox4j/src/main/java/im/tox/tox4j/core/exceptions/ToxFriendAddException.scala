package im.tox.tox4j.core.exceptions

import im.tox.tox4j.exceptions.ToxException

object ToxFriendAddException {

  sealed trait Code extends ToxException.Code
  /**
   * A friend request has already been sent, or the address belongs to a friend
   * that is already on the friend list. To resend a friend request, first remove
   * the friend, and then call addFriend again.
   */
  case object ALREADY_SENT extends Code
  /**
   * The friend address checksum failed.
   */
  case object BAD_CHECKSUM extends Code
  /**
   * A memory allocation failed when trying to increase the friend list size.
   */
  case object MALLOC extends Code
  /**
   * The friend request message was empty. This, and the TOO_LONG code will
   * never be returned from [[ToxCore.addFriendNoRequest]].
   */
  case object NO_MESSAGE extends Code
  /**
   * An argument was null.
   */
  case object NULL extends Code
  /**
   * The friend address belongs to the sending client.
   */
  case object OWN_KEY extends Code
  /**
   * The friend was already on the friend list, but the noSpam value was different.
   */
  case object SET_NEW_NOSPAM extends Code
  /**
   * The length of the friend request message exceeded [[ToxCoreConstants.MAX_FRIEND_REQUEST_LENGTH]].
   */
  case object TOO_LONG extends Code

}

final case class ToxFriendAddException(code: ToxFriendAddException.Code, message: String = "")
  extends ToxException[ToxFriendAddException.Code](message)
