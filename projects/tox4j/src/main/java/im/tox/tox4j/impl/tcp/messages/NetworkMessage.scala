package im.tox.tox4j.impl.tcp.messages

import im.tox.tox4j.impl.tcp.PublicKey

/**
 * A general message that can be send over a network.
 *
 * @constructor Creates a new NetworkMessage with a target.
 * @param source The public key of the message sender.
 * @param target The target (where the message should go) or the public key
 *               of the sending client for bootstrapping.
 * @param content The payload of the message.
 */
final case class NetworkMessage(source: PublicKey, target: PublicKey, content: Payload) extends Serializable
