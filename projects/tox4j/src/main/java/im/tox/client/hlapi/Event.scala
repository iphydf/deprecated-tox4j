package im.tox.client.hlapi

sealed trait Event

object Event {

  sealed abstract class NetworkEvent extends Event
  sealed abstract class UiEvent extends Event
  /**
   * Network Events
   */
  //  Self connection status (connected/disconnected)
  final case class ReceiveSelfConnectionStatus() extends NetworkEvent
  //  Receive file transmission control from friends
  final case class ReceiveFileTransmissionControl() extends NetworkEvent
  //  Receive file transmission request from friends
  final case class ReceiveFileTransmissionRequest() extends NetworkEvent
  //  Receive a chunk of file under transmission from friends
  final case class ReceiveFileChunk() extends NetworkEvent
  //  A friend’s connection status changes (online/offline)
  final case class ReceiveFriendConnectionStatusChange() extends NetworkEvent
  //  Receive a message from a friend
  final case class ReceiveFriendMessage() extends NetworkEvent
  //  Receive a message from a group
  final case class ReceiveGroupMessage() extends NetworkEvent
  //  A friend’s name changes
  final case class ReceiveFriendNameChange() extends NetworkEvent
  //  Receive a friend request
  final case class ReceiveFriendRequest() extends NetworkEvent
  //  A friend’s user status changes
  final case class ReceiveFriendUserStatusChange() extends NetworkEvent
  //  A friend’s status message changes
  final case class ReceiveFriendStatusMessageChange() extends NetworkEvent
  //  A friend typing status changes
  final case class ReceiveFriendTypingStatusChange() extends NetworkEvent
  //  A lossy packet arrives
  final case class ReceiveLossyPacket() extends NetworkEvent
  //  A lossless packet arrives
  final case class ReceiveLosslessPacket() extends NetworkEvent
  //  Receive the read receipt of a message
  final case class ReceiveReadReceipt() extends NetworkEvent

  /**
   * GUI Events
   */
  //  Send a friend request
  final case class SendFriendRequest(friendId: String, request: String) extends UiEvent
  //  Delete a friend
  final case class DeleteFriend(friendId: String) extends UiEvent
  //  See the details of a friend’s profile
  final case class RequestFriendProfile(friendId: String) extends UiEvent
  //  Change personal profile information
  final case class UpdateSelfProfile(profile: String) extends UiEvent
  //  Change a friend’s alias
  final case class ChangeFriendAlias(friendId: String, newAlias: String) extends UiEvent
  //  Change a group conversation’s alias
  final case class ChangeGroupAlias(groupId: String, newAlias: String) extends UiEvent
  //  Send a request to join a group
  final case class SendJoinGroupConversationRequest(groupId: String, request: String) extends UiEvent
  //  Invite a friend to a group chat
  final case class InviteFriendToGroupRequest(groupId: String, friendId: String, request: String) extends UiEvent
  //  Create a group chat
  final case class CreateGroup(groupName: String, option: String) extends UiEvent
  //  Initiate a conversation with a friend
  final case class CreatePrivateConversation(friendId: String) extends UiEvent
  //  Initiate a group conversation
  final case class CreateGroupConversation(groupId: String) extends UiEvent
  //  Remove a member from a group chat
  final case class RemoveMemberFromGroupConversation(groupId: String, friendId: String) extends UiEvent
  //  Dismiss a group
  final case class DismissGroupConversation(groupId: String) extends UiEvent
  //  Leave a group conversation
  final case class LeaveGroupConversation(groupId: String) extends UiEvent
  //  Delete a conversation
  final case class DeleteConversation(conversationId: String) extends UiEvent
  //  Send a text message to a conversation (group/private)
  final case class SendTextMessage(conversationId: String, message: String) extends UiEvent
  //  Initiate a file transmission request to a friend
  final case class SendFileTransmissionRequest(friendId: String, fileDescription: String) extends UiEvent
  //  Get all conversations
  final case class GetConversationList() extends UiEvent
  //  Get all friends
  final case class GetFriendList() extends UiEvent
  //  Get the messages associated with a conversation
  final case class GetMessageList(conversationId: String) extends UiEvent
  //  Get the file sent history with a friend
  final case class GetFileSentList(conversationId: String) extends UiEvent
  //  Login
  final case class Login(username: String, password: String) extends UiEvent
  // Logout
  final case class Logout() extends UiEvent
  //  Set status message
  final case class ChangeStatusMessage(newStatusMessage: String) extends UiEvent
  //  Change self user status
  final case class ChangeUserStatus(status: String) extends UiEvent
  //  Change self connection status
  final case class ChangeConnectionStatus(status: String) extends UiEvent
  //  Block/unblock a friend
  final case class ChangeFriendBlockStatus(friendId: String) extends UiEvent
  //  Mute/unmute a conversation
  final case class ChangeConversationMuteStatus(friendId: String) extends UiEvent
  //  Star/unstar a friend
  final case class ChangeConversationStarStatus(friendId: String) extends UiEvent

}
