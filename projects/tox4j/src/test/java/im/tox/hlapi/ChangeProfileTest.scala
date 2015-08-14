package im.tox.hlapi

import im.tox.hlapi.event.UiEvent.{ SetStatusMessageEvent, SendFriendMessageEvent, SetNicknameEvent }
import im.tox.hlapi.request.Reply.{ GetFriendProfileReply, GetSelfProfileReply, GetFriendListReply }
import im.tox.hlapi.request.Request.{ GetFriendProfileRequest, GetSelfProfileRequest, GetFriendListRequest }
import im.tox.hlapi.state.ConnectionState.ConnectionStatus
import im.tox.hlapi.state.MessageState.Message
import im.tox.hlapi.state.{ FriendState, CoreState }

final class ChangeProfileTest extends BrownConyTestBase {

  override def newChatClient(friendName: String, expectedFriendName: String) =
    new ChatClient(friendName, expectedFriendName) {
      private var initialName = true
      private var initialStatusMessage = true
      override def receiveFriendConnectionStatus(friendNumber: Int, connectionStatus: ConnectionStatus): Unit = {
        if (isBrown) {
          brownAdapter.acceptUiEvent(SetNicknameEvent("Brownie".getBytes))
          debug("change name to Brownie")
          val reply = brownAdapter.acceptRequest(GetSelfProfileRequest())
          reply match {
            case GetSelfProfileReply(name, statusMessage) => {
              assert(name.deep == "Brownie".getBytes.deep)
            }
          }
        }
      }
      override def receiveFriendName(friendNumber: Int, name: Array[Byte]): Unit = {
        debug("receive friend's name change")
        if (initialName) {
          initialName = false
        } else {
          assert(isCony)
          assert(name.deep == "Brownie".getBytes.deep)
          debug("receive Brown's new name")
          val reply = conyAdapter.acceptRequest(GetFriendProfileRequest(friendNumber))
          reply match {
            case GetFriendProfileReply(name, statusMessage) => {
              assert(name.deep == "Brownie".getBytes.deep)
            }
          }
          conyAdapter.acceptUiEvent(SendFriendMessageEvent(friendNumber, "please change your status message".getBytes))
          debug("ask Brown to change status message")
        }
      }
      override def receiveFriendMessage(friendNumber: Int, message: Message): Unit = {
        assert(isBrown)
        assert(message.content.deep == "please change your status message".getBytes.deep)
        brownAdapter.acceptUiEvent(SetStatusMessageEvent("I like Cony".getBytes))
        debug("change status message")
        val reply = brownAdapter.acceptRequest(GetSelfProfileRequest())
        reply match {
          case GetSelfProfileReply(name, statusMessage) => {
            assert(statusMessage.deep == "I like Cony".getBytes.deep)
          }
        }
        brownFinished = true

      }
      override def receiveFriendStatusMessage(friendNumber: Int, statusMessage: Array[Byte]): Unit = {
        if (initialStatusMessage) {
          initialStatusMessage = false
        } else {
          assert(isCony)
          assert(statusMessage.deep == "I like Cony".getBytes.deep)
          debug("see Brown changed status message")
          val reply = conyAdapter.acceptRequest(GetFriendProfileRequest(friendNumber))
          reply match {
            case GetFriendProfileReply(name, statusm) => {
              assert(statusm.deep == statusMessage.deep)
            }
          }
          conyFinished = true
        }
      }
    }
}
