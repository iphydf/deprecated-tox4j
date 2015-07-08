package im.tox.client

//context free grammer linear temporal logic
package object hlapi {

  sealed abstract class ToxEntity[T]
  sealed abstract class ToxList[T]
  sealed abstract class Attribute[T, ToxEntity] extends Gettable
  sealed trait Sendable[T, Conversation]
  sealed trait Addable[T]
  sealed trait Settable[ToxEntity] extends Attribute[Settable, ToxEntity]
  sealed trait Fillable[ToxEntity] extends Attribute[Fillable, ToxEntity]
  trait Gettable[Attribute]

  sealed abstract class ToxEvent[T]
  sealed abstract class Send[T, Sendable] extends ToxEvent[Send]
  sealed abstract class Add[T, Addable[A], ToxList[A]] extends ToxEvent[Add]
  sealed abstract class Set[T, Settable](target: ToxEntity) extends ToxEvent[Set]
  sealed abstract class Fill[T, Fillable](target: ToxEntity, input: String) extends ToxEvent[Fill]

  trait Request extends ToxEvent[Request]

  sealed abstract class Conversation[T](cid: String) extends ToxEntity[Conversation] with Addable[Conversation[T]]
  final case class GroupConversation(gcid: String) extends Conversation[GroupConversation](gcid)
  final case class PrivateConversation(pcid: String) extends Conversation[PrivateConversation](pcid)

  sealed abstract class ConversationList[T]() extends ToxList[ConversationList]
  final case class GroupConversationList() extends ConversationList[GroupConversationList]()
  final case class PrivateConversationList() extends ConversationList[PrivateConversationList]()

  final case class User(uid: String) extends ToxEntity[User] with Addable[User]
  final case class FriendList() extends ToxList[FriendList]
  final case class GroupConversationMemberList(gcid: String) extends ToxList[GroupConversationMemberList]

  final case class Message(mid: String, cid: String) extends Sendable[Message, Conversation] with Addable[Message]
  final case class MessageList(cid: String) extends ToxList[MessageList]

  final case class File(fid: String, pcid: String) extends Sendable[File, PrivateConversation] with Addable[File]
  final case class FileList(pcid: String) extends ToxList[FileList]

  final case class AudioCall(user: User) extends Sendable[AudioCall, PrivateConversation]
  final case class VideoCall(user: User) extends Sendable[VideoCall, PrivateConversation]

  final case class ConversationAlias[Conversation](input: String) extends Fillable[ConversationAlias](input)
  final case class UserAlias[User](input: String) extends Fillable[UserAlias](input)
  final case class Mute[Conversation]() extends Settable[Mute]
  final case class Star[Conversation]() extends Settable[Star]
  final case class Block[PrivateConversation]() extends Settable[Block]

  final case class SendMessage(message: Message, conversation: Conversation) extends Send[SendMessage, Message]
  final case class SendFileRequest(file: File, privateConversation: PrivateConversation)
    extends Send[SendFileRequest, File] with Request
  final case class SendAudioCallRequest(user: User) extends Send[SendAudioCallRequest, AudioCall] with Request
  final case class SendVideoCallRequest(user: User) extends Send[SendVideoCallRequest, VideoCall] with Request

  final case class AddFriendRequest(user: User) extends Add[AddFriendRequest, User, FriendList]
  final case class AddGroupConversationRequest(groupConversation: GroupConversation)
    extends Add[AddGroupConversationRequest, GroupConversation, GroupConversationList] with Request
  final case class AddFriendToGroupConversationRequest(user: User, groupConversation: GroupConversation)
    extends Add[AddFriendToGroupConversationRequest, User, GroupConversationMemberList]

  final case class FillGroupConversationAlias(groupConversation: GroupConversation, input: String)
    extends Fill[FillGroupConversationAlias, ConversationAlias](groupConversation, input)
  final case class SetStar(conversation: Conversation) extends Set[SetStar, Star](conversation)
  final case class SetMute(conversation: Conversation) extends Set[SetMute, Mute](conversation)
  final case class SetBlock(conversation: Conversation) extends Set[SetBlock, Block](conversation)
}

