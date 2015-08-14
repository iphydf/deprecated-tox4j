package im.tox.hlapi

import im.tox.hlapi.adapter.ToxAdapter
import im.tox.hlapi.event.UiEvent.{ AddFriendNoRequestEvent, SendFriendRequestEvent, DeleteFriendEvent }
import im.tox.hlapi.request.Reply.GetFriendListReply
import im.tox.hlapi.request.Request.GetFriendListRequest
import im.tox.hlapi.state.ConnectionState.{ Disconnect, Connect, ConnectionStatus }
import im.tox.hlapi.state.FriendState.FriendRequest
import im.tox.hlapi.state.PublicKeyState.PublicKey

final class AddDeleteFriendTest extends BrownConyTestBase {
  override def newChatClient(name: String, expectedFriendName: String) = new ChatClient(name, expectedFriendName) {
    private var friendDeleted: Boolean = false
    override def receiveFriendConnectionStatus(friendNumber: Int, connectionStatus: ConnectionStatus): Unit = {
      connectionStatus match {
        case Connect(connectionOptions) => {
          val selfAdapter = {
            if (isBrown) {
              brownAdapter
            } else {
              conyAdapter
            }
          }
          if (!friendDeleted) {
            selfAdapter.acceptUiEvent(DeleteFriendEvent(friendNumber))
            debug("delete friend " + expectedFriendName)
            friendDeleted = true
            val reply = selfAdapter.acceptRequest(GetFriendListRequest())
            reply match {
              case GetFriendListReply(friendList) => {
                assert(friendList.size == 0)
              }
            }
          } else {
            debug("see friend appear online again")
            if (isBrown) {
              brownFinished = true
            } else {
              conyFinished = true
            }
          }
        }
        case Disconnect() => {
          if (isBrown) {
            brownAdapter.acceptUiEvent(SendFriendRequestEvent(conyAddress, FriendRequest("I am Brown".getBytes)))
            debug("add Cony back")
            val reply = brownAdapter.acceptRequest(GetFriendListRequest())
            reply match {
              case GetFriendListReply(friendList) => {
                assert(friendList.size == 1)
              }
            }
          }
        }
      }
    }
    override def receiveFriendRequest(publicKey: PublicKey, friendRequestMessage: FriendRequest): Unit = {
      assert(isCony)
      debug("Brown send me friend request.")
      conyAdapter.acceptUiEvent(AddFriendNoRequestEvent(publicKey))
      val reply = conyAdapter.acceptRequest(GetFriendListRequest())
      reply match {
        case GetFriendListReply(friendList) => {
          assert(friendList.size == 1)
        }
      }
    }
  }
}
