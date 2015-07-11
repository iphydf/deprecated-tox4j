package im.tox.tox4j.core.exceptions

import im.tox.tox4j.exceptions.ToxException

object ToxFriendCustomPacketException {

  sealed trait Code extends ToxException.Code
  /**
   * Attempted to send an empty packet.
   */
  case object EMPTY extends Code
  /**
   * This client is currently not connected to the friend.
   */
  case object FRIEND_NOT_CONNECTED extends Code
  /**
   * The friendNumber passed did not designate a valid friend.
   */
  case object FRIEND_NOT_FOUND extends Code
  /**
   * The first byte of data was not in the specified range for the packet type.
   * This range is 200-254 for lossy, and 160-191 for lossless packets.
   */
  case object INVALID extends Code
  /**
   * An argument was null.
   */
  case object NULL extends Code
  /**
   * An allocation error occurred while increasing the send queue size.
   */
  case object SENDQ extends Code
  /**
   * Packet data length exceeded [[ToxCoreConstants.MAX_CUSTOM_PACKET_SIZE]].
   */
  case object TOO_LONG extends Code

}

final case class ToxFriendCustomPacketException(code: ToxFriendCustomPacketException.Code, message: String = "")
  extends ToxException[ToxFriendCustomPacketException.Code](message)
