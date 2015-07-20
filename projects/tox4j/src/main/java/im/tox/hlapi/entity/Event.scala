package im.tox.hlapi.entity

import im.tox.hlapi.adapter.ToxClientListener
import im.tox.hlapi.entity.CoreState._

sealed trait Event

object Event {

  sealed abstract class NetworkEvent extends Event
  sealed abstract class UiEvent extends Event
  sealed abstract class SelfEvent extends Event
  sealed abstract class ReplyEvent extends Event

  /**
   * Network Events
   */
  //  Self connection status (connected/disconnected)
  final case class ReceiveSelfConnectionStatusEvent(connectionStatus: ConnectionStatus) extends NetworkEvent
  //  Receive file transmission control from friends
  final case class ReceiveFileTransmissionControlEvent() extends NetworkEvent
  //  Receive file transmission request from friends
  final case class ReceiveFileTransmissionRequestEvent() extends NetworkEvent
  //  Receive a chunk of file under transmission from friends
  final case class ReceiveFileChunkEvent() extends NetworkEvent
  //  A friend’s connection status changes (online/offline)
  final case class ReceiveFriendConnectionStatusEvent(friendNumber: Int, connectionStatus: ConnectionStatus) extends NetworkEvent
  //  Receive a message from a friend
  final case class ReceiveFriendMessageEvent(friendNumber: Int, messageType: MessageType, timeDelta: Int, content: Array[Byte]) extends NetworkEvent
  //  A friend’s name changes
  final case class ReceiveFriendNameEvent(friendNumber: Int, name: Array[Byte]) extends NetworkEvent
  //  Receive a friend request
  final case class ReceiveFriendRequestEvent() extends NetworkEvent
  //  A friend’s user status changes
  final case class ReceiveFriendStatusEvent(friendNumber: Int, userStatus: UserStatus) extends NetworkEvent
  //  A friend’s status message changes
  final case class ReceiveFriendStatusMessageEvent(friendNumber: Int, statusMessage: Array[Byte]) extends NetworkEvent
  //  A friend typing status changes
  final case class ReceiveFriendTypingEvent(friendNumber: Int, isTyping: Boolean) extends NetworkEvent
  //  A lossy packet arrives
  final case class ReceiveLossyPacketEvent() extends NetworkEvent
  //  A lossless packet arrives
  final case class ReceiveLosslessPacketEvent() extends NetworkEvent
  //  Receive the read receipt of a message
  final case class ReceiveFriendReadReceiptEvent(friendNumber: Int, messageId: Int) extends NetworkEvent

  /**
   * GUI Events
   */
  final case class RegisterEventListener(toxClientListener: ToxClientListener) extends UiEvent
  //  Send a friend request
  final case class SendFriendRequestEvent(publicKey: PublicKey, request: Option[FriendRequestMessage]) extends UiEvent
  //  Delete a friend
  final case class DeleteFriendEvent(friendNumber: Int) extends UiEvent
  //  Change user nickname
  final case class SetNicknameEvent(nickname: Array[Byte]) extends UiEvent
  //  Send a text message to a private conversation
  final case class SendFriendMessageEvent(friendNumber: Int, message: Message) extends UiEvent
  //  Initiate a file transmission request to a friend
  final case class SendFileTransmissionRequest(friendId: String, fileDescription: String) extends UiEvent
  //  Get all conversations
  final case class GetPrivateConversationList() extends UiEvent
  //  Get all friends
  final case class GetFriendList() extends UiEvent
  //  Set status message
  final case class SetStatusMessageEvent(statusMessage: Array[Byte]) extends UiEvent
  //  Change self user status
  final case class SetUserStatusEvent(status: UserStatus) extends UiEvent
  //  Change self connection
  final case class SetConnectionStatusEvent(status: ConnectionStatus) extends UiEvent
  //  Get the messages associated with a conversation
  final case class GetMessageList(friendNumber: Int) extends UiEvent
  //  Get the file sent history with a friend
  final case class GetFileSentList(friendNumber: Int) extends UiEvent
  final case class GetPublicKeyEvent() extends UiEvent
  /**
   * Self Events, only can be called by HLAPI
   */
  final case class AddToFriendList(friendNumber: Int, friend: Friend) extends SelfEvent
  final case class GetSelfPublicKeyEvent() extends SelfEvent

  /**
   * Reply Events
   */
  final case class RequestSuccess() extends ReplyEvent()
  final case class Error() extends ReplyEvent()
  final case class ReplyGetFriendListRequest(friendList: FriendList) extends ReplyEvent()
  final case class ReplyGetFriendMessageListRequest(messageList: MessageList) extends ReplyEvent()
  final case class ReplyGetPublicKeyRequest(publicKey: Array[Byte]) extends ReplyEvent()

  /**
   * Not handleable event now
   */
  /*
  //  Change a friend’s alias
  final case class ChangeFriendAlias(friendId: Int, newAlias: String) extends UiEvent
  //  Change a group conversation’s alias
  final case class ChangeGroupAlias(groupNumber: Int, newAlias: String) extends UiEvent
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
  final case class RemoveMemberFromGroupConversation(groupNumber: Int, friendNumber: Int) extends UiEvent
  //  Dismiss a group
  final case class DismissGroupConversation(groupId: String) extends UiEvent
  //  Leave a group conversation
  final case class LeaveGroupConversation(groupId: String) extends UiEvent
  //  Delete a conversation
  final case class DeleteConversation(conversationId: String) extends UiEvent
  //  See the details of a friend’s profile
  final case class RequestFriendProfile(friendId: String) extends UiEvent
  //  Block/unblock a friend
  final case class ChangeFriendBlockStatus(friend: Int) extends UiEvent
  //  Mute/unmute a private conversation
  final case class ChangeFriendConversationMuteStatus(friendNumber: Int) extends UiEvent
  //  Mute/unmute a group conversation
  final case class ChangeGroupConversationMuteStatus(groupNumber: Int) extends UiEvent
  //  Star/unstar a friend
  final case class ChangeFriendStarStatus(friendNumber: Int) extends UiEvent
  //  Start/unstar a group
  final case class ChangeGroupStarStatus(groupNumber: Int) extends UiEvent
  // Logout
  final case class Logout() extends UiEvent
  //  Send a text message to a group conversation
  final case class SendPublicMessage(groupNumber: Int, message: Message) extends UiEvent
  */
}

