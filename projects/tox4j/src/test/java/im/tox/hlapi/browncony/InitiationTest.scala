package im.tox.hlapi.browncony

import im.tox.hlapi.request.Reply.{ GetFriendListReply, GetFriendPublicKeyReply, GetSelfPublicKeyReply }
import im.tox.hlapi.request.Request.{ GetFriendListRequest, GetFriendPublicKeyRequest, GetSelfPublicKeyRequest }
import im.tox.hlapi.state.ConnectionState.ConnectionStatus

final class InitiationTest extends BrownConyTestBase {
  override def newChatClient(name: String, friendName: String) = new BrownConyChatClient(name, friendName) {

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
