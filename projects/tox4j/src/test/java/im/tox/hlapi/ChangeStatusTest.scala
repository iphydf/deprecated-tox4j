package im.tox.hlapi

import im.tox.hlapi.event.UiEvent.{ SetUserStatusEvent, SendFriendMessageEvent }
import im.tox.hlapi.request.Reply.{ GetSelfStatusReply, GetFriendUserStatusReply, GetFriendConnectionStatusReply }
import im.tox.hlapi.request.Request.{ GetSelfStatusRequest, GetFriendUserStatusRequest, GetFriendConnectionStatusRequest }
import im.tox.hlapi.state.ConnectionState.{ Disconnect, ConnectionOptions, Connect, ConnectionStatus }
import im.tox.hlapi.state.MessageState
import im.tox.hlapi.state.MessageState.Message
import im.tox.hlapi.state.UserStatusState.{ Online, Busy, Away, UserStatus }

final class ChangeStatusTest extends BrownConyTestBase {
  override def newChatClient(name: String, expectedFriendName: String) = new ChatClient(name, expectedFriendName) {
    var conyMessage = 0
    override def receiveFriendConnectionStatus(friendNumber: Int, connectionStatus: ConnectionStatus): Unit = {
      val selfAdapter = {
        if (isBrown) {
          brownAdapter
        } else {
          conyAdapter
        }
      }
      debug("receive friend's connection status")
      val reply = selfAdapter.acceptRequest(GetFriendConnectionStatusRequest(friendNumber))
      reply match {
        case GetFriendConnectionStatusReply(status) => {
          assert(connectionStatus == status)
        }
      }
      if (connectionStatus == Disconnect()) {
        assert(isCony)
        debug("see Brown go offline")
        conyFinished = true
      }
    }
    override def receiveFriendStatus(friendNumber: Int, userStatus: UserStatus): Unit = {
      val selfAdapter = {
        if (isBrown) {
          brownAdapter
        } else {
          conyAdapter
        }
      }
      val reply = selfAdapter.acceptRequest(GetFriendUserStatusRequest(friendNumber))
      reply match {
        case GetFriendUserStatusReply(status) => {
          assert(status == userStatus)
        }
      }
      if (isCony) {
        if (conyMessage == 0) {
          conyAdapter.acceptUiEvent(SendFriendMessageEvent(friendNumber, "away".getBytes))
          debug("ask Brown to change user status to away")
          conyMessage += 1
        } else if (conyMessage == 1) {
          assert(userStatus == Away())
          conyAdapter.acceptUiEvent(SendFriendMessageEvent(friendNumber, "busy".getBytes))
          debug("ask Brown to change user status to busy")
          conyMessage += 1
        } else if (conyMessage == 2) {
          assert(userStatus == Busy())
          conyAdapter.acceptUiEvent(SendFriendMessageEvent(friendNumber, "online".getBytes))
          debug("ask Brown to change user status to online")
          conyMessage += 1
        } else if (conyMessage == 3) {
          assert(userStatus == Online())
          conyAdapter.acceptUiEvent(SendFriendMessageEvent(friendNumber, "disconnect".getBytes))
          debug("ask Brown to disconnect")
        }
      }
    }

    override def receiveFriendMessage(friendNumber: Int, message: Message): Unit = {
      assert(isBrown)
      if (MessageState.messageContentL.get(message).deep == "away".getBytes.deep) {
        brownAdapter.acceptUiEvent(SetUserStatusEvent(Away()))
        debug("change user status to away")
        val reply = brownAdapter.acceptRequest(GetSelfStatusRequest())
        reply match {
          case GetSelfStatusReply(status) => {
            assert(status == Away())
          }
        }
      } else if (MessageState.messageContentL.get(message).deep == "busy".getBytes.deep) {
        brownAdapter.acceptUiEvent(SetUserStatusEvent(Busy()))
        debug("change user status to busy")
        val reply = brownAdapter.acceptRequest(GetSelfStatusRequest())
        reply match {
          case GetSelfStatusReply(status) => {
            assert(status == Busy())
          }
        }
      } else if (MessageState.messageContentL.get(message).deep == "online".getBytes.deep) {
        brownAdapter.acceptUiEvent(SetUserStatusEvent(Online()))
        debug("change user status to online")
        val reply = brownAdapter.acceptRequest(GetSelfStatusRequest())
        reply match {
          case GetSelfStatusReply(status) => {
            assert(status == Online())
          }
        }
      } else if (MessageState.messageContentL.get(message).deep == "disconnect".getBytes.deep) {
        brownAdapter.closeToxSession()
        debug("say byebye")
        brownFinished = true
      }
    }

  }
}
