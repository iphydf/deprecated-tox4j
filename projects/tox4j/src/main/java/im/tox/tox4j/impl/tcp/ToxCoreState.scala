package im.tox.tox4j.impl.tcp

import scala.collection.immutable.Queue

import im.tox.tox4j.impl.tcp.messages.NetworkMessage

/**
 * The friend list is saved reverse, that means new friends are placed at
 * the beginning of the list (because that's much more efficient).
 * Because of that, the friend '0' is the last item in the list (friend numbers
 * don't change) and the first item has the friendNumber length - 1.
 */
final case class ToxCoreState(
  self: FriendState,
  noSpam: NoSpam,
  closed: Boolean,
  connection: Option[NetworkConnection],
  messages: Queue[NetworkMessage],
  friends: Map[Int, FriendState],
  serverKey: Option[PublicKey],
  callbacks: CallbacksState
)
