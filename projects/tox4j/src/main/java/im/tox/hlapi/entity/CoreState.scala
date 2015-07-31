package im.tox.hlapi.entity

import scalaz.Lens

object CoreState {

  val userProfileL = Lens.lensu[ToxState, UserProfile](
    (a, value) => a.copy(userProfile = value),
    _.userProfile
  )

  val nicknameL = Lens.lensu[UserProfile, Array[Byte]](
    (a, value) => a.copy(nickname = value),
    _.nickname
  )

  val publicKeyL = Lens.lensu[ToxState, PublicKey](
    (a, value) => a.copy(publicKey = value),
    _.publicKey
  )

  val statusMessageL = Lens.lensu[UserProfile, Array[Byte]](
    (a, value) => a.copy(statusMessage = value),
    _.statusMessage
  )

  val connectionStatusL = Lens.lensu[ToxState, ConnectionStatus](
    (a, value) => a.copy(connectionStatus = value),
    _.connectionStatus
  )

  val userStatusL = Lens.lensu[ToxState, UserStatus](
    (a, value) => a.copy(userStatus = value),
    _.userStatus
  )

  val friendListL = Lens.lensu[ToxState, FriendList](
    (a, value) => a.copy(friendList = value),
    _.friendList
  )

  val friendListFriendsL = Lens.lensu[FriendList, Map[Int, Friend]](
    (a, value) => a.copy(friends = value),
    _.friends
  )

  val friendConversationL = Lens.lensu[Friend, FriendConversation](
    (a, value) => a.copy(conversation = value),
    _.conversation
  )

  val ConversationMessageListL = Lens.lensu[FriendConversation, MessageList](
    (a, value) => a.copy(messageList = value),
    _.messageList
  )

  val MessageListMessagesL = Lens.lensu[MessageList, Map[Int, Message]](
    (a, value) => a.copy(messages = value),
    _.messages
  )

  val friendProfileL = Lens.lensu[Friend, UserProfile](
    (a, value) => a.copy(userProfile = value),
    _.userProfile
  )

  val friendUserStatusL = Lens.lensu[Friend, UserStatus](
    (a, value) => a.copy(userStatus = value),
    _.userStatus
  )

  val friendConnectionStatusL = Lens.lensu[Friend, ConnectionStatus](
    (a, value) => a.copy(connectionStatus = value),
    _.connectionStatus
  )

  val friendPublicKeyL = Lens.lensu[Friend, PublicKey](
    (a, value) => a.copy(publicKey = value),
    _.publicKey
  )

  val conversationIsTypingL = Lens.lensu[FriendConversation, Boolean](
    (a, value) => a.copy(isTyping = value),
    _.isTyping
  )

  val messageStatusL = Lens.lensu[Message, MessageStatus](
    (a, value) => a.copy(messageStatus = value),
    _.messageStatus
  )

  val conversationFileSentListL = Lens.lensu[FriendConversation, FileList](
    (a, value) => a.copy(fileList = value),
  _.fileList
  )

  val fileListFilesL = Lens.lensu[FileList, Map[Int, File]](
    (a, value) => a.copy(files = value),
  _.files
  )

  val fileFileStatusL = Lens.lensu[File, FileStatus](
    (a, value) => a.copy(fileSendStatus = value),
  _.fileSendStatus
  )

  val stateNicknameL = userProfileL >=> nicknameL
  val stateStatusMessageL = userProfileL >=> statusMessageL
  val friendMessageListL = friendConversationL >=> ConversationMessageListL
  val friendMessagesL = friendMessageListL >=> MessageListMessagesL
  val friendNameL = friendProfileL >=> nicknameL
  val friendStatusMessageL = friendProfileL >=> statusMessageL
  val friendIsTypingL = friendConversationL >=> conversationIsTypingL
  val friendsL = friendListL >=> friendListFriendsL
  val friendFilesL = friendConversationL >=> conversationFileSentListL >=> fileListFilesL

  final case class ToxState(
    userProfile: UserProfile = UserProfile(),
    connectionStatus: ConnectionStatus = Disconnect(),
    userStatus: UserStatus = Offline(),
    friendList: FriendList = FriendList(),
    publicKey: PublicKey = PublicKey()
  )

  final case class UserProfile(nickname: Array[Byte] = Array.ofDim(0), statusMessage: Array[Byte] = Array.ofDim(0))

  final case class FriendConversation(
    isTyping: Boolean = false,
    messageList: MessageList = MessageList(),
    fileList: FileList = FileList()
  )

  final case class Friend(
    userProfile: UserProfile = UserProfile(),
    userStatus: UserStatus = Offline(),
    connectionStatus: ConnectionStatus = Disconnect(),
    conversation: FriendConversation = FriendConversation(),
    publicKey: PublicKey = PublicKey()
  )

  final case class File(fileName: String,
                        fileData: Array[Byte],
                        fileKind: FileKind,
                        fileSendStatus: FileStatus = RequestInitiated())

  final case class FriendList(friends: Map[Int, Friend] = Map[Int, Friend]())
  final case class MessageList(messages: Map[Int, Message] = Map[Int, Message]())
  final case class FileList(files: Map[Int, File] = Map[Int, File]())

  final case class Message(messageType: MessageType, timeDelta: Int, content: Array[Byte], messageStatus: MessageStatus)

  sealed abstract class MessageStatus
  final case class MessageSent() extends MessageStatus
  final case class MessageRead() extends MessageStatus
  final case class MessageReceived() extends MessageStatus

  sealed abstract class MessageType
  final case class NormalMessage() extends MessageType
  final case class ActionMessage() extends MessageType

  sealed abstract class ConnectionStatus
  final case class Connect(connectionOptions: ConnectionOptions) extends ConnectionStatus
  final case class Disconnect() extends ConnectionStatus

  sealed abstract class UserStatus
  final case class Online() extends UserStatus
  final case class Away() extends UserStatus
  final case class Busy() extends UserStatus
  final case class Offline() extends UserStatus

  final case class ConnectionOptions(
    enableIPv6: Boolean = true,
    enableUdp: Boolean = true,
    proxyOption: ProxyOption = NoProxy(),
    saveDataOption: SaveDataOption = NoSaveData()
  )

  sealed abstract class ProxyOption
  final case class Http(proxyHost: String, proxyPort: Int) extends ProxyOption
  final case class Socks5(proxyHost: String, proxyPort: Int) extends ProxyOption
  final case class NoProxy() extends ProxyOption

  sealed abstract class SaveDataOption
  final case class NoSaveData() extends SaveDataOption
  final case class ToxSave(data: Array[Byte]) extends SaveDataOption

  sealed abstract class FileKind
  final case class Data() extends FileKind
  final case class Avatar() extends FileKind

  sealed abstract class FileStatus
  final case class RequestInitiated() extends FileStatus
  final case class RequestSent() extends FileStatus
  final case class RequestAccepted() extends FileStatus
  final case class InTransmission() extends FileStatus
  final case class Paused() extends FileStatus
  final case class FileReceived() extends FileStatus
  final case class FileSent() extends FileStatus

  final case class PublicKey(key: Array[Byte] = Array.ofDim[Byte](0))
  final case class FriendRequestMessage(request: Array[Byte] = Array.ofDim[Byte](0))
}
