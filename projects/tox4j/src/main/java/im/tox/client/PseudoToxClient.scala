package im.tox.client

import im.tox.client.hlapi.Event
import im.tox.client.hlapi.Event._
import im.tox.client.hlapi.State._

final class PseudoToxClient {

  def acceptEvent(state: ToxState, e: Event): ToxState = {
    e match {
      case e: NetworkEvent => handleNetworkEvent(state, e)
      case e: UiEvent      => handleUiEvent(state, e)
    }
  }

  private def handleNetworkEvent(state: ToxState, e: NetworkEvent): ToxState = {
    e match {

      case ReceiveSelfConnectionStatus()         => state
      //  Receive file transmission control from friends
      case ReceiveFileTransmissionControl()      => state
      //  Receive file transmission request from friends
      case ReceiveFileTransmissionRequest()      => state
      //  Receive a chunk of file under transmission from friends
      case ReceiveFileChunk()                    => state
      //  A friend’s connection status changes (online/offline)
      case ReceiveFriendConnectionStatusChange() => state
      //  Receive a message from a friend
      case ReceiveFriendMessage()                => state
      //  Receive a message from a group
      case ReceiveGroupMessage()                 => state
      //  A friend’s name changes
      case ReceiveFriendNameChange()             => state
      //  Receive a friend request
      case ReceiveFriendRequest()                => state
      //  A friend’s user status changes
      case ReceiveFriendUserStatusChange()       => state
      //  A friend’s status message changes
      case ReceiveFriendStatusMessageChange()    => state
      //  A friend typing status changes
      case ReceiveFriendTypingStatusChange()     => state
      //  A lossy packet arrives
      case ReceiveLossyPacket()                  => state
      //  A lossless packet arrives
      case ReceiveLosslessPacket()               => state
      //  Receive the read receipt of a message
      case ReceiveReadReceipt()                  => state

    }
  }

  private def handleUiEvent(state: ToxState, e: UiEvent): ToxState = {
    e match {

      case ChangeConnectionStatus(status) => state.copy(connectionStatus = status)
      case ChangeUserStatus(status) => state.copy(userStatus = status)
      case ChangeStatusMessage(newStatusMessage) => state.copy(userProfile = state.userProfile.copy(nickName = newStatusMessage))
      case ChangeNickname(nickname) => state.copy(userProfile = state.userProfile.copy(statusMessage = nickname))
      case SendFriendRequest(friendId, request) => state
      //  Delete a friend
      case DeleteFriend(friendId) => state
      //  See the details of a friend’s profile
      case RequestFriendProfile(friendId) => state
      //  Change a friend’s alias
      case ChangeFriendAlias(friendId, newAlias) => state
      //  Change a group conversation’s alias
      case ChangeGroupAlias(groupId, newAlias) => state
      //  Send a request to join a group
      case SendJoinGroupConversationRequest(groupId, request) => state
      //  Invite a friend to a group chat
      case InviteFriendToGroupRequest(groupId, friendId, request) => state
      //  Create a group chat
      case CreateGroup(groupName, option) => state
      //  Initiate a conversation with a friend
      case CreatePrivateConversation(friendId) => state
      //  Initiate a group conversation
      case CreateGroupConversation(groupId) => state
      //  Remove a member from a group chat
      case RemoveMemberFromGroupConversation(groupId, friendId) => state
      //  Dismiss a group
      case DismissGroupConversation(groupId) => state
      //  Leave a group conversation
      case LeaveGroupConversation(groupId) => state
      //  Delete a conversation
      case DeleteConversation(conversationId) => state
      //  Send a text message to a conversation (group/private)
      case SendTextMessage(conversationId, message) => state
      //  Initiate a file transmission request to a friend
      case SendFileTransmissionRequest(friendId, fileDescription) => state
      //  Get all conversations
      case GetConversationList() => state
      //  Get all friends
      case GetFriendList() => state
      //  Get the messages associated with a conversation
      case GetMessageList(conversationId) => state
      //  Get the file sent history with a friend
      case GetFileSentList(conversationId) => state
      //  Login
      case Login(username, password) => state
      // Logout
      case Logout() => state
      //  Block/unblock a friend
      case ChangeFriendBlockStatus(friendId) => state
      //  Mute/unmute a conversation
      case ChangeConversationMuteStatus(friendId) => state
      //  Star/unstar a friend
      case ChangeConversationStarStatus(friendId) => state

    }
  }

}
