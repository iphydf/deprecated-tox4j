package im.tox.hlapi

import im.tox.hlapi.event.UiEvent.{ SetTypingEvent, SendFriendMessageEvent }
import im.tox.hlapi.request.Reply.{ GetFriendReceivedMessageReply, GetFriendSentMessageReply, GetFriendSentMessageListReply, GetFriendListReply }
import im.tox.hlapi.request.Request.{ GetFriendReceivedMessageRequest, GetFriendSentMessageRequest, GetFriendSentMessageListRequest, GetFriendListRequest }
import im.tox.hlapi.state.ConnectionState.ConnectionStatus
import im.tox.hlapi.state.MessageState
import im.tox.hlapi.state.MessageState._

final class SendMessageTest extends BrownConyTestBase {
  override def newChatClient(friendName: String, expectedFriendName: String) = new ChatClient(friendName, expectedFriendName) {
    override def receiveFriendConnectionStatus(friendNumber: Int, connectionStatus: ConnectionStatus): Unit = {
      if (isBrown) {
        brownAdapter.acceptUiEvent(SetTypingEvent(0, true))
        debug("is typing")
        brownAdapter.acceptUiEvent(SendFriendMessageEvent(0, "I like Cony".getBytes))
        debug("send Cony message")
        val reply = brownAdapter.acceptRequest(GetFriendSentMessageListRequest(0))
        reply match {
          case GetFriendSentMessageListReply(messageList: MessageList) => {
            assert(messageList.size == 1)
          }
        }
        val message = brownAdapter.acceptRequest(GetFriendSentMessageRequest(0, 1))
        message match {
          case GetFriendSentMessageReply(message: Message) => {
            assert(MessageState.messageStatusL.get(message) == MessageSent())
            assert(MessageState.messageContentL.get(message).deep == "I like Cony".getBytes.deep)
            assert(MessageState.messageTypeL.get(message) == NormalMessage())
          }
        }
      }
    }

    override def receiveFriendMessage(friendNumber: Int, message: Message): Unit = {
      assert(isCony)
      assert(MessageState.messageContentL.get(message).deep == "I like Cony".getBytes.deep)
      debug("receive Brown's message")
      val reply = conyAdapter.acceptRequest(GetFriendReceivedMessageRequest(0, 1))
      reply match {
        case GetFriendReceivedMessageReply(brownMessage) => {
          assert(MessageState.messageStatusL.get(brownMessage) == MessageReceived())
          assert(MessageState.messageContentL.get(brownMessage).deep == "I like Cony".getBytes.deep)
          assert(MessageState.messageTypeL.get(brownMessage) == NormalMessage())
        }
      }
      conyFinished = true
    }

    override def receiveFriendReadReceipt(friendNumber: Int, messageId: Int): Unit = {
      assert(isBrown)
      debug("receive message read receipt")
      val reply = brownAdapter.acceptRequest(GetFriendSentMessageRequest(friendNumber, messageId))
      reply match {
        case GetFriendSentMessageReply(message) => {
          assert(MessageState.messageStatusL.get(message) == MessageRead())
        }
      }
      brownFinished = true
    }
  }
}
