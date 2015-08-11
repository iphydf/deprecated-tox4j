package im.tox.hlapi.listener

import im.tox.hlapi.state.ConnectionState.ConnectionStatus
import im.tox.hlapi.state.FriendState.FriendRequest
import im.tox.hlapi.state.MessageState.Message
import im.tox.hlapi.state.PublicKeyState.PublicKey
import im.tox.hlapi.state.UserStatusState.UserStatus

/*
  Client should implement this
 */
trait ToxClientListener {

  def receiveSelfConnectionStatus(connectionStatus: ConnectionStatus)
  def receiveFriendConnectionStatus(friendNumber: Int, connectionStatus: ConnectionStatus)
  def receiveFriendMessage(friendNumber: Int, message: Message)
  def receiveFriendName(friendNumber: Int, name: Array[Byte])
  def receiveFriendStatus(friendNumber: Int, userStatus: UserStatus)
  def receiveFriendStatusMessage(friendNumber: Int, statusMessage: Array[Byte])
  def receiveFriendTyping(friendNumber: Int, isTyping: Boolean)
  def receiveFriendReadReceipt(friendNumber: Int, messageId: Int)
  def receiveFriendRequest(publicKey: PublicKey, friendRequest: FriendRequest)
}
