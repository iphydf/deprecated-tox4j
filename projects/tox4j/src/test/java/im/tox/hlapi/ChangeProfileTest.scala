package im.tox.hlapi

import im.tox.hlapi.adapter.ToxAdapter
import im.tox.hlapi.event.Event.UiEventType
import im.tox.hlapi.event.UiEvent.{ SetStatusMessageEvent, SendFriendMessageEvent, SetNicknameEvent }
import im.tox.hlapi.request.Reply.{ GetSelfProfileReply, GetFriendListReply }
import im.tox.hlapi.request.Request.{ GetSelfProfileRequest, GetFriendListRequest }
import im.tox.hlapi.state.ConnectionState.ConnectionStatus
import im.tox.hlapi.state.MessageState.Message
import im.tox.hlapi.state.{ FriendState, CoreState }

final class ChangeProfileTest extends BrownConyTestBase {
  override def newChatClient(friendName: String, expectedFriendName: String, adapter: ToxAdapter) =
    new ChatClient(friendName, expectedFriendName, adapter) {
      override def receiveFriendConnectionStatus(friendNumber: Int, connectionStatus: ConnectionStatus): Unit = {
        if (isBrown) {
          selfAdapter.acceptEvent(UiEventType(SetNicknameEvent("Brownie".getBytes)))
          debug("change name to Brownie")
          val reply = selfAdapter.acceptRequest(GetSelfProfileRequest())
          reply match {
            case GetSelfProfileReply(name) => {
              assert(name.nickname.deep == "Brownie".getBytes.deep)
            }
          }
        }
      }
      override def receiveFriendName(friendNumber: Int, name: Array[Byte]): Unit = {
        assert(isCony)
        assert(name.deep == "Brownie".getBytes.deep)
        debug("receive Brown's new name")
        val reply = selfAdapter.acceptRequest(GetFriendListRequest())
        reply match {
          case GetFriendListReply(friendList) => {
            val brownName = FriendState.friendNameL.get(friendList.friends.apply(friendNumber))
            assert(brownName.deep == "Brownie".getBytes.deep)
          }
        }
        selfAdapter.acceptEvent(UiEventType(SendFriendMessageEvent(friendNumber, "please change your status message".getBytes)))
        debug("ask Brown to change status message")
      }
      override def receiveFriendMessage(friendNumber: Int, message: Message): Unit = {
        assert(isBrown)
        assert(message.content.deep == "please change your status message".getBytes.deep)
        selfAdapter.acceptEvent(UiEventType(SetStatusMessageEvent("I like Cony".getBytes)))
        debug("change status message")
        val reply = selfAdapter.acceptRequest(GetSelfProfileRequest())
        reply match {
          case GetSelfProfileReply(profile) => {
            assert(profile.statusMessage.deep == "I like Cony".getBytes.deep)
          }
        }
      }
      override def receiveFriendStatusMessage(friendNumber: Int, statusMessage: Array[Byte]): Unit = {
        assert(isCony)
      }
    }
}
