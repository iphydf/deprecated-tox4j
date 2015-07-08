package im.tox.client

import im.tox.client.hlapi.Event
import im.tox.client.hlapi.Event._

abstract class PseudoToxClient {

  def acceptEvent(e: Event): Unit = e match {
    case e: NetworkEvent => handleNetworkEvent(e)
    case e: UiEvent => handleUiEvent(e)
  }

  protected def handleNetworkEvent(e: NetworkEvent): Unit = e match {
      
    case ReceiveSelfConnectionStatus() =>
    //  Receive file transmission control from friends
    case ReceiveFileTransmissionControl() =>
    //  Receive file transmission request from friends
    case ReceiveFileTransmissionRequest() =>
    //  Receive a chunk of file under transmission from friends
    case ReceiveFileChunk() =>
    //  A friend’s connection status changes (online/offline)
    case ReceiveFriendConnectionStatusChange() =>
    //  Receive a message from a friend
    case ReceiveFriendMessage() =>
    //  Receive a message from a group
    case ReceiveGroupMessage() =>
    //  A friend’s name changes
    case ReceiveFriendNameChange() =>
    //  Receive a friend request
    case ReceiveFriendRequest() =>
    //  A friend’s user status changes
    case ReceiveFriendUserStatusChange() =>
    //  A friend’s status message changes
    case ReceiveFriendStatusMessageChange() =>
    //  A friend typing status changes
    case ReceiveFriendTypingStatusChange() =>
    //  A lossy packet arrives
    case ReceiveLossyPacket() =>
    //  A lossless packet arrives
    case ReceiveLosslessPacket() =>
    //  Receive the read receipt of a message
    case ReceiveReadReceipt() =>

  }
  protected def handleUiEvent(e: UiEvent): Unit = {
    case SendFriendRequest(friendId: String, request: String) =>
    //  Delete a friend
    case DeleteFriend(friendId: String) =>
    //  See the details of a friend’s profile
    case RequestFriendProfile(friendId: String) =>
    //  Change personal profile information
    case UpdateSelfProfile(profile: String) =>
    //  Change a friend’s alias
    case ChangeFriendAlias(friendId: String, newAlias: String) =>
    //  Change a group conversation’s alias
    case ChangeGroupAlias(groupId: String, newAlias: String) =>
    //  Send a request to join a group
    case SendJoinGroupConversationRequest(groupId: String, request: String) =>
    //  Invite a friend to a group chat
    case InviteFriendToGroupRequest(groupId: String, friendId: String, request: String) =>
    //  Create a group chat
    case CreateGroup(groupName: String, option: String) =>
    //  Initiate a conversation with a friend
    case CreatePrivateConversation(friendId: String) =>
    //  Initiate a group conversation
    case CreateGroupConversation(groupId: String) =>
    //  Remove a member from a group chat
    case RemoveMemberFromGroupConversation(groupId: String, friendId: String) =>
    //  Dismiss a group
    case DismissGroupConversation(groupId: String) =>
    //  Leave a group conversation
    case LeaveGroupConversation(groupId: String) =>
    //  Delete a conversation
    case DeleteConversation(conversationId: String) =>
    //  Send a text message to a conversation (group/private)
    case SendTextMessage(conversationId: String, message: String) =>
    //  Initiate a file transmission request to a friend
    case SendFileTransmissionRequest(friendId: String, fileDescription: String) =>
    //  Get all conversations
    case GetConversationList() =>
    //  Get all friends
    case GetFriendList() =>
    //  Get the messages associated with a conversation
    case GetMessageList(conversationId: String) =>
    //  Get the file sent history with a friend
    case GetFileSentList(conversationId: String) =>
    //  Login
    case Login(username: String, password: String) =>
    // Logout
    case Logout() =>
    //  Set status message
    case ChangeStatusMessage(newStatusMessage: String) =>
    //  Change self user status
    case ChangeUserStatus(status: String) =>
    //  Change self connection status
    case ChangeConnectionStatus(status: String) =>
    //  Block/unblock a friend
    case ChangeFriendBlockStatus(friendId: String) =>
    //  Mute/unmute a conversation
    case ChangeConversationMuteStatus(friendId: String) =>
    //  Star/unstar a friend
    case ChangeConversationStarStatus(friendId: String) =>

  }

}
