package im.tox.hlapi.adapter.parser

import im.tox.hlapi.event.NetworkEvent
import im.tox.hlapi.event.NetworkEvent._
import im.tox.hlapi.listener.ToxClientListener

object ClientNotifier {
  def notify(listener: ToxClientListener, e: NetworkEvent): Unit = {
    e match {
      case ReceiveFriendConnectionStatusEvent(friendNumber, connectionStatus) =>
        listener.receiveFriendConnectionStatus(friendNumber, connectionStatus)
      case ReceiveFriendMessageEvent(friendNumber, message) =>
        listener.receiveFriendMessage(friendNumber, message)
      case ReceiveFriendNameEvent(friendNumber, name) =>
        listener.receiveFriendName(friendNumber, name)
      case ReceiveFriendRequestEvent(publicKey, request) =>
        listener.receiveFriendRequest(publicKey, request)
      case ReceiveFriendStatusEvent(friendNumber, userStatus) =>
        listener.receiveFriendStatus(friendNumber, userStatus)
      case ReceiveFriendStatusMessageEvent(friendNumber, statusMessage) =>
        listener.receiveFriendStatusMessage(friendNumber, statusMessage)
      case ReceiveFriendTypingEvent(friendNumber, isTyping) =>
        listener.receiveFriendTyping(friendNumber, isTyping)
      case ReceiveFriendReadReceiptEvent(friendNumber, messageId) =>
        listener.receiveFriendReadReceipt(friendNumber, messageId)
    }
  }
}
