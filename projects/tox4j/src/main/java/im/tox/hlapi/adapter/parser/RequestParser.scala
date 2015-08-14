package im.tox.hlapi.adapter.parser

import im.tox.hlapi.request.Reply._
import im.tox.hlapi.request.Request._
import im.tox.hlapi.request.{ Reply, Request }
import im.tox.hlapi.state.{ FriendState, CoreState }
import im.tox.hlapi.state.CoreState.ToxState

object RequestParser {

  def parseRequest(request: Request, state: ToxState): Reply = {
    request match {
      case GetSelfProfileRequest() => GetSelfProfileReply(
        CoreState.stateNicknameL.get(state),
        CoreState.stateStatusMessageL.get(state)
      )
      case GetSelfAddressRequest()   => GetSelfAddressReply(CoreState.addressL.get(state))
      case GetSelfPublicKeyRequest() => GetSelfPublicKeyReply(CoreState.publicKeyL.get(state))
      case GetFriendListRequest()    => GetFriendListReply(CoreState.friendListL.get(state))
      case GetFriendProfileRequest(friendNumber) => {
        val friend = CoreState.friendsL.get(state)(friendNumber)
        GetFriendProfileReply(FriendState.friendNameL.get(friend), FriendState.friendStatusMessageL.get(friend))
      }
      case GetFriendPublicKeyRequest(friendNumber) => {
        val friend = CoreState.friendsL.get(state)(friendNumber)
        GetFriendPublicKeyReply(FriendState.friendPublicKeyL.get(friend))
      }
      case GetFriendSentMessageListRequest(friendNumber) => {
        val friend = CoreState.friendsL.get(state)(friendNumber)
        GetFriendSentMessageListReply(FriendState.friendSentMessageListL.get(friend))
      }
      case GetFriendSentMessageRequest(friendNumber, messageId) => {
        val friend = CoreState.friendsL.get(state)(friendNumber)
        GetFriendSentMessageReply(FriendState.friendSentMessagesL.get(friend).apply(messageId))
      }
      case GetFriendReceivedMessageListRequest(friendNumber) => {
        val friend = CoreState.friendsL.get(state)(friendNumber)
        GetFriendReceivedMessageListReply(FriendState.friendReceivedMessageListL.get(friend))
      }
      case GetFriendReceivedMessageRequest(friendNumber, messageId) => {
        val friend = CoreState.friendsL.get(state)(friendNumber)
        GetFriendReceivedMessageReply(FriendState.friendReceivedMessagesL.get(friend).apply(messageId))
      }
      case GetFriendConnectionStatusRequest(friendNumber) => {
        val friend = CoreState.friendsL.get(state)(friendNumber)
        GetFriendConnectionStatusReply(FriendState.friendConnectionStatusL.get(friend))
      }
      case GetFriendUserStatusRequest(friendNumber) => {
        val friend = CoreState.friendsL.get(state)(friendNumber)
        GetFriendUserStatusReply(FriendState.friendUserStatusL.get(friend))
      }
      case GetSelfStatusRequest() => {
        GetSelfStatusReply(CoreState.userStatusL.get(state))
      }

    }
  }
}
