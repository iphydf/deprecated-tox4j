package im.tox.hlapi.multiclients

import com.typesafe.scalalogging.Logger
import im.tox.hlapi.listener.ToxClientListener
import im.tox.hlapi.state.ConnectionState.ConnectionStatus
import im.tox.hlapi.state.FriendState.FriendRequest
import im.tox.hlapi.state.MessageState.Message
import im.tox.hlapi.state.PublicKeyState.PublicKey
import im.tox.hlapi.state.UserStatusState.UserStatus
import org.slf4j.LoggerFactory

abstract class ChatClient(id: Int) extends ToxClientListener {

  protected val logger = Logger(LoggerFactory.getLogger(classOf[MultiClientTestBase]))

  protected def debug(info: String): Unit = {
    logger.info("Client[" + id + "]: " + info)
  }

  override def receiveSelfConnectionStatus(connectionStatus: ConnectionStatus): Unit = {}
  override def receiveFriendConnectionStatus(friendNumber: Int, connectionStatus: ConnectionStatus): Unit = {}
  override def receiveFriendMessage(friendNumber: Int, message: Message): Unit = {}
  override def receiveFriendName(friendNumber: Int, name: Array[Byte]): Unit = {}
  override def receiveFriendStatus(friendNumber: Int, userStatus: UserStatus): Unit = {}
  override def receiveFriendStatusMessage(friendNumber: Int, statusMessage: Array[Byte]): Unit = {}
  override def receiveFriendTyping(friendNumber: Int, isTyping: Boolean): Unit = {}
  override def receiveFriendReadReceipt(friendNumber: Int, messageId: Int): Unit = {}
  override def receiveFriendRequest(publicKey: PublicKey, friendRequest: FriendRequest): Unit = {}
}
