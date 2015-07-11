package im.tox.tox4j.core.exceptions

import im.tox.tox4j.exceptions.ToxException

object ToxFriendSendMessageException {

  sealed trait Code extends ToxException.Code
  /**
   * Attempted to send a zero-length message.
   */
  case object EMPTY extends Code
  /**
   * This client is currently not connected to the friend.
   */
  case object FRIEND_NOT_CONNECTED extends Code
  /**
   * The friend number did not designate a valid friend.
   */
  case object FRIEND_NOT_FOUND extends Code
  /**
   * An argument was null.
   */
  case object NULL extends Code
  /**
   * An allocation error occurred while increasing the send queue size.
   */
  case object SENDQ extends Code
  /**
   * Message length exceeded [[ToxCoreConstants.MAX_MESSAGE_LENGTH]].
   */
  case object TOO_LONG extends Code

}

final case class ToxFriendSendMessageException(code: ToxFriendSendMessageException.Code, message: String = "")
  extends ToxException[ToxFriendSendMessageException.Code](message)
