package im.tox.hlapi

import im.tox.hlapi.adapter.ToxAdapter
import im.tox.hlapi.request.Reply.{ GetFriendPublicKeyReply, GetFriendListReply, GetSelfPublicKeyReply }
import im.tox.hlapi.request.Request.{ GetFriendPublicKeyRequest, GetFriendListRequest, GetSelfPublicKeyRequest }
import im.tox.hlapi.state.ConnectionState.ConnectionStatus

final class InitiationTest extends BrownConyTestBase {
  override def newChatClient(name: String, friendName: String) = new ChatClient(name, friendName) {

    override def receiveFriendConnectionStatus(friendNumber: Int, connectionStatus: ConnectionStatus): Unit = {
      val selfAdapter = {
        if (isBrown) {
          brownAdapter
        } else {
          conyAdapter
        }
      }
      val publicKeyReply = selfAdapter.acceptRequest(GetSelfPublicKeyRequest())
      publicKeyReply match {
        case GetSelfPublicKeyReply(publicKey) => {
          val friendListReply = selfAdapter.acceptRequest(GetFriendListRequest())
          friendListReply match {
            case GetFriendListReply(friendList) => {
              assert(friendList.size == 1)
              if (isBrown) {
                val reply = brownAdapter.acceptRequest(GetFriendPublicKeyRequest(friendNumber))
                reply match {
                  case GetFriendPublicKeyReply(publicKey) => {
                    assert(publicKey.key.deep == conyPublicKey.key.deep)
                  }
                }
                debug("has Cony as friend now.")
              } else {
                val reply = conyAdapter.acceptRequest(GetFriendPublicKeyRequest(friendNumber))
                reply match {
                  case GetFriendPublicKeyReply(publicKey) => {
                    assert(publicKey.key.deep == brownPublicKey.key.deep)
                  }
                }
                debug("has Brown as friend now.")
              }
            }
          }
          if (isBrown) {
            brownFinished = true
          } else {
            conyFinished = true
          }
        }
      }
    }
  }
}
