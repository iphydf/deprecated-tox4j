package im.tox.client.hlapi

object Event {
  /**
   * Network Events
   */
  //  Self connection status (connected/disconnected)
  case class ReceiveSelfConnectionStatus()
  //  Receive file transmission control from friends
  case class ReceiveFileTransmissionControl()
  //  Receive file transmission request from friends
  case class ReceiveFileTransmissionRequest()
  //  Receive a chunk of file under transmission from friends
  case class ReceiveFileChunk()
  //  A friend’s connection status changes (online/offline)
  case class ReceiveFriendConnectionStatusChange()
  //  Receive a message from a friend
  case class ReceiveFriendMessage()
  //  Receive a message from a group
  case class ReceiveGroupMessage()
  //  A friend’s name changes
  case class ReceiveFriendNameChange()
  //  Receive a friend request
  case class ReceiveFriendRequest()
  //  A friend’s user status changes
  case class ReceiveFriendUserStatusChange()
  //  A friend’s status message changes
  case class ReceiveFriendStatusMessageChange()
  //  A friend typing status changes
  case class ReceiveFriendTypingStatusChange()
  //  A lossy packet arrives
  case class ReceiveLossyPacket()
  //  A lossless packet arrives
  case class ReceiveLosslessPacket()
  //  Receive the read receipt of a message
  case class ReceiveReadReceipt()

  /**
   * GUI Events
   */
  //  Send a friend request
  case class SendFriendRequest(friendId: String, request: String)
  //  Delete a friend
  case class DeleteFriend(friendId: String)
  //  See the details of a friend’s profile
  case class RequestFriendProfile(friendId: String)
  //  Change personal profile information
  case class UpdateSelfProfile(profile: String)
  //  Change a friend’s alias
  case class ChangeFriendAlias(friendId: String, newAlias: String)
  //  Change a group conversation’s alias
  case class ChangeGroupAlias(groupId: String, newAlias: String)
  //  Send a request to join a group
  case class SendJoinGroupConversationRequest(groupId: String, request: String)
  //  Invite a friend to a group chat
  case class InviteFriendToGroupRequest(groupId: String, friendId: String, request: String)
  //  Create a group chat
  case class CreateGroup()
  //  Initiate a conversation with a friend
  case class CreatePrivateConversation(friendId: String)
  //  Initiate a group conversation
  case class CreateGroupConversation(groupId: String)
  //  Remove a member from a group chat
  case class RemoveMemberFromGroupConversation(groupId: String, friendId: String)
  //  Dismiss a group
  case class DismissGroupConversation(groupId: String)
  //  Leave a group conversation
  case class LeaveGroupConversation(groupId: String)
  //  Delete a conversation
  case class DeleteConversation(conversationId: String)
  //  Send a text message to a conversation (group/private)
  case class SendTextMessage(conversationId: String, message: String)
  //  Initiate a file transmission request to a friend
  case class SendFileTransmissionRequest(friendId: String, fileDescription: String)
  //  Get all conversations
  case class GetConversationList()
  //  Get all friends
  case class GetFriendList()
  //  Get the messages associated with a conversation
  case class GetMessageList(conversationId: String)
  //  Get the file sent history with a friend
  case class GetFileSentList(conversationId: String)
  //  Login
  case class Login(username: String, password: String)
  // Logout
  case class Logout()
  //  Set status message
  case class ChangeStatusMessage(newStatusMessage: String)
  //  Change self user status
  case class ChangeUserStatus(status: String)
  //  Change self connection status
  case class ChangeConnectionStatus(status: String)
  //  Block/unblock a friend
  case class ChangeFriendBlockStatus(friendId: String)
  //  Mute/unmute a conversation
  case class ChangeConversationMuteStatus(friendId: String)
  //  Star/unstar a friend
  case class ChangeConversationStarStatus(friendId: String)

}
