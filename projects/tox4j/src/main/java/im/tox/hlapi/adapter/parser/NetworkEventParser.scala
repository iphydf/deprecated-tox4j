package im.tox.hlapi.adapter.parser

import im.tox.hlapi.action.DiskIOAction
import im.tox.hlapi.event.NetworkEvent
import im.tox.hlapi.event.NetworkEvent._
import im.tox.hlapi.listener.ToxClientListener
import im.tox.hlapi.state.ConnectionState.ConnectionStatus
import im.tox.hlapi.state.CoreState._
import im.tox.hlapi.state.FriendState.Friend
import im.tox.hlapi.state.MessageState.{ Message, MessageRead, MessageReceived }
import im.tox.hlapi.state.UserStatusState.UserStatus
import im.tox.hlapi.state.{ CoreState, FriendState, MessageState }

object NetworkEventParser extends EventParser {

  def parse(state: ToxState, e: NetworkEvent): (ToxState, Option[DiskIOAction]) = {
    e match {

      case ReceiveFileTransmissionControlEvent() => {
        (state, None)
      }
      case ReceiveFileTransmissionRequestEvent() => {
        (state, None)
      }
      case ReceiveFileChunkEvent() => {
        (state, None)
      }
      case ReceiveFriendConnectionStatusEvent(friendNumber, status) => {
        var nextState = state
        if (!friendExist(friendNumber, state)) {
          nextState = CoreState.friendsL.set(
            nextState,
            CoreState.friendsL.get(nextState) + ((friendNumber, Friend()))
          )
        }
        (FriendState.friendSetHandler[ConnectionStatus](friendNumber, nextState, FriendState.friendConnectionStatusL, status), None)
      }
      case ReceiveFriendMessageEvent(friendNumber, message) => {
        val friend = friendsL.get(state)(friendNumber)
        val nextState = FriendState.friendSetHandler[Map[Int, Message]](friendNumber, state, FriendState.friendReceivedMessagesL,
          FriendState.friendReceivedMessagesL.get(friend) + ((FriendState.friendReceivedMessagesL.get(friend).size + 1, message)))
        (nextState, None)
      }
      case ReceiveFriendNameEvent(friendNumber, name) => {
        (FriendState.friendSetHandler[Array[Byte]](friendNumber, state, FriendState.friendNameL, name), None)
      }
      case ReceiveFriendRequestEvent(publicKey, request) => {
        (state, None)
      }
      case ReceiveFriendStatusEvent(friendNumber, status) => {
        (FriendState.friendSetHandler[UserStatus](friendNumber, state, FriendState.friendUserStatusL, status), None)
      }
      case ReceiveFriendStatusMessageEvent(friendNumber, statusMessage) => {
        (FriendState.friendSetHandler[Array[Byte]](friendNumber, state, FriendState.friendStatusMessageL, statusMessage), None)

      }
      case ReceiveFriendTypingEvent(friendNumber, isTyping) => {
        (FriendState.friendSetHandler[Boolean](friendNumber, state, FriendState.friendIsTypingL, isTyping), None)
      }
      case ReceiveLossyPacketEvent() => {
        (state, None)
      }
      case ReceiveLosslessPacketEvent() => {
        (state, None)
      }
      case ReceiveFriendReadReceiptEvent(friendNumber, messageId) => {
        val friend = friendsL.get(state)(friendNumber)
        (FriendState.friendSetHandler[Map[Int, Message]](friendNumber, state, FriendState.friendSentMessagesL,
          FriendState.friendSentMessagesL.get(friend).updated(
            messageId,
            MessageState.messageStatusL.set(FriendState.friendSentMessagesL.get(friend)(messageId), MessageRead())
          )), None)
      }
    }
  }
}
