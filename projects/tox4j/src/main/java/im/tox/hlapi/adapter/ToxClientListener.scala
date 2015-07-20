package im.tox.hlapi.adapter

import im.tox.hlapi.entity.CoreState.{ UserStatus, Message, ConnectionStatus }

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

}
