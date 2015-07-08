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
  case class ReceiveFriendConnectionStatus()
  //  Receive a message from a friend/group
  case class ReceiveMessage()
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
  case class SendFriendRequest()
  //  Delete a friend
  case class DeleteFriend()
  //  See the details of a friend’s profile
  case class RequestFriendProfile()
  //  Change personal profile information
  case class UpdateSelfProfile()
  //  Change a friend’s alias
  case class ChangeFriendAlias()
  //  Change a group conversation’s alias
  case class ChangeGroupConversationAlias()
  //  Send a request to join a group
  case class SendJoinGroupConversationRequest()
  //  Invite a friend to a group chat
  case class InviteFriendToGroupConversationRequest()
  //  Create a group chat
  case class CreateGroupConversation()
  //  Initiate a conversation with a friend
  case class CreatePrivateConversation()
  //  Remove a member from a group chat
  case class RemoveMemberFromGroupConversation()
  //  Dismiss a group
  case class DismissGroupConversation()
  //  Leave a group conversation
  case class LeaveGroupConversation()
  //  Delete a conversation
  case class DeleteConversation()
  //  Send a text message to a conversation (group/private)
  case class SendTextMessage()
  //  Initiate a file transmission request to a friend
  case class SendFileTransmissionRequest()
  //  Get all conversations
  case class GetConversationList()
  //  Get all friends
  case class GetFriendList()
  //  Get the messages associated with a conversation
  case class GetMessageList()
  //  Get the file sent history with a friend
  case class GetFileSentList()
  //  Login
  case class Login()
  // Logout
  case class Logout()
  //  Set status message
  case class ChangeStatusMessage()
  //  Change self user status
  case class ChangeUserStatus()
  //  Change self connection status
  case class ChangeConnectionStatus()
  //  Block/unblock a friend
  case class ChangeFriendBlockStatus()
  //  Mute/unmute a conversation
  case class ChangeConversationMuteStatus()
  //  Star/unstar a friend
  case class ChangeConversationStarStatus()

}
