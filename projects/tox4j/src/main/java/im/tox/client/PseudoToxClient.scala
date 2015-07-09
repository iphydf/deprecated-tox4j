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
    state
    /*e match {

      case ReceiveSelfConnectionStatus()         =>
      //  Receive file transmission control from friends
      case ReceiveFileTransmissionControl()      =>
      //  Receive file transmission request from friends
      case ReceiveFileTransmissionRequest()      =>
      //  Receive a chunk of file under transmission from friends
      case ReceiveFileChunk()                    =>
      //  A friend’s connection status changes (online/offline)
      case ReceiveFriendConnectionStatusChange() =>
      //  Receive a message from a friend
      case ReceiveFriendMessage()                =>
      //  Receive a message from a group
      case ReceiveGroupMessage()                 =>
      //  A friend’s name changes
      case ReceiveFriendNameChange()             =>
      //  Receive a friend request
      case ReceiveFriendRequest()                =>
      //  A friend’s user status changes
      case ReceiveFriendUserStatusChange()       =>
      //  A friend’s status message changes
      case ReceiveFriendStatusMessageChange()    =>
      //  A friend typing status changes
      case ReceiveFriendTypingStatusChange()     =>
      //  A lossy packet arrives
      case ReceiveLossyPacket()                  =>
      //  A lossless packet arrives
      case ReceiveLosslessPacket()               =>
      //  Receive the read receipt of a message
      case ReceiveReadReceipt()                  =>

    }*/
  }

  private def handleUiEvent(state: ToxState, e: UiEvent): ToxState = {
    e match {

      case ChangeConnectionStatus(status)        => ConnectionStatusHandler(state, e)
      case ChangeUserStatus(status)              => UserStatusHandler(state, e)
      case ChangeStatusMessage(newStatusMessage) => StatusMessageHandler(state, e)
      case ChangeNickname(nickname)              => NicknameHandler(state, e)
      case _                                     => state
      /*
      case SendFriendRequest(friendId, request) =>
      //  Delete a friend
      case DeleteFriend(friendId) =>
      //  See the details of a friend’s profile
      case RequestFriendProfile(friendId) =>
      //  Change a friend’s alias
      case ChangeFriendAlias(friendId, newAlias) =>
      //  Change a group conversation’s alias
      case ChangeGroupAlias(groupId, newAlias) =>
      //  Send a request to join a group
      case SendJoinGroupConversationRequest(groupId, request) =>
      //  Invite a friend to a group chat
      case InviteFriendToGroupRequest(groupId, friendId, request) =>
      //  Create a group chat
      case CreateGroup(groupName, option) =>
      //  Initiate a conversation with a friend
      case CreatePrivateConversation(friendId) =>
      //  Initiate a group conversation
      case CreateGroupConversation(groupId) =>
      //  Remove a member from a group chat
      case RemoveMemberFromGroupConversation(groupId, friendId) =>
      //  Dismiss a group
      case DismissGroupConversation(groupId) =>
      //  Leave a group conversation
      case LeaveGroupConversation(groupId) =>
      //  Delete a conversation
      case DeleteConversation(conversationId) =>
      //  Send a text message to a conversation (group/private)
      case SendTextMessage(conversationId, message) =>
      //  Initiate a file transmission request to a friend
      case SendFileTransmissionRequest(friendId, fileDescription) =>
      //  Get all conversations
      case GetConversationList() =>
      //  Get all friends
      case GetFriendList() =>
      //  Get the messages associated with a conversation
      case GetMessageList(conversationId) =>
      //  Get the file sent history with a friend
      case GetFileSentList(conversationId) =>
      //  Login
      case Login(username, password) =>
      // Logout
      case Logout() =>
      //  Block/unblock a friend
      case ChangeFriendBlockStatus(friendId) =>
      //  Mute/unmute a conversation
      case ChangeConversationMuteStatus(friendId) =>
      //  Star/unstar a friend
      case ChangeConversationStarStatus(friendId) =>
*/
    }
  }

  private def ConnectionStatusHandler(toxState: ToxState, e: UiEvent): ToxState = {
    toxState.copy(connectionStatus = "blah")
  }

  private def UserStatusHandler(toxState: ToxState, e: UiEvent): ToxState = {
    toxState.copy(userStatus = "blah")
  }

  private def NicknameHandler(toxState: ToxState, e: UiEvent): ToxState = {
    toxState.copy(userProfile = toxState.userProfile.copy(nickName = "blah"))
  }

  private def StatusMessageHandler(toxState: ToxState, e: UiEvent): ToxState = {
    toxState.copy(userProfile = toxState.userProfile.copy(statusMessage = "blah"))
  }
}
