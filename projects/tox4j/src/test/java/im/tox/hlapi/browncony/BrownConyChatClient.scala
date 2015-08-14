package im.tox.hlapi.browncony

import com.typesafe.scalalogging.Logger
import im.tox.hlapi.listener.ToxClientListener
import im.tox.hlapi.state.ConnectionState.ConnectionStatus
import im.tox.hlapi.state.FriendState.FriendRequest
import im.tox.hlapi.state.MessageState.Message
import im.tox.hlapi.state.PublicKeyState.PublicKey
import im.tox.hlapi.state.UserStatusState.UserStatus
import org.slf4j.LoggerFactory

abstract class BrownConyChatClient(name: String, friendName: String) extends ToxClientListener {

  protected val logger = Logger(LoggerFactory.getLogger(classOf[BrownConyTestBase]))

  protected def isBrown: Boolean = {
    name == "Brown"
  }

  protected def isCony: Boolean = {
    name == "Cony"
  }

  protected def debug(info: String): Unit = {
    logger.info(name + ": " + info)
  }

  override def receiveSelfConnectionStatus(connectionStatus: ConnectionStatus): Unit = {}
  override def receiveFriendConnectionStatus(friendNumber: Int, connectionStatus: ConnectionStatus): Unit = {}
  override def receiveFriendMessage(friendNumber: Int, message: Message): Unit = {}
  override def receiveFriendName(friendNumber: Int, name: Array[Byte]): Unit = {}
  override def receiveFriendStatus(friendNumber: Int, userStatus: UserStatus): Unit = {}
  override def receiveFriendStatusMessage(friendNumber: Int, statusMessage: Array[Byte]): Unit = {}
  override def receiveFriendTyping(friendNumber: Int, isTyping: Boolean): Unit = {}
  override def receiveFriendReadReceipt(friendNumber: Int, messageId: Int): Unit = {}
  def receiveFriendRequest(publicKey: PublicKey, friendRequest: FriendRequest): Unit = {}
}