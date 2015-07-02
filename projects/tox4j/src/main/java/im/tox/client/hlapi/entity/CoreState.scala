package im.tox.client.hlapi.entity

import scalaz.Lens

object CoreState {

  val userProfileL = Lens.lensu[ToxState, UserProfile](
    (a, value) => a.copy(userProfile = value),
    _.userProfile
  )

  val nicknameL = Lens.lensu[UserProfile, String](
    (a, value) => a.copy(nickname = value),
    _.nickname
  )

  val publicKeyL = Lens.lensu[ToxState, PublicKey](
    (a, value) => a.copy(publicKey = PublicKey),
    _.publicKey
  )

  val statusMessageL = Lens.lensu[UserProfile, String](
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

  val friendsL = Lens.lensu[ToxState, Map[Int, Friend]](
    (a, value) => a.copy(friends = value),
    _.friends
  )

  val friendConversationL = Lens.lensu[Friend, FriendConversation](
    (a, value) => a.copy(conversation = value),
    _.conversation
  )

  val friendConversationMessagesL = Lens.lensu[FriendConversation, Map[Int, Message]](
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
    (a, value) => a.copy(publicKey = PublicKey),
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

  val stateNicknameL = userProfileL >=> nicknameL
  val stateStatusMessageL = userProfileL >=> statusMessageL
  val friendMessagesL = friendConversationL >=> friendConversationMessagesL
  val friendNameL = friendProfileL >=> nicknameL
  val friendIsTypingL = friendConversationL >=> conversationIsTypingL

  final case class ToxState(userProfile: UserProfile = UserProfile(), connectionStatus: ConnectionStatus = Disconnect(),
    userStatus: UserStatus = Offline(),
    friends: Map[Int, Friend] = Map[Int, Friend](), publicKey: PublicKey = PublicKey())

  final case class UserProfile(nickname: String = "", statusMessage: String = "")

  final case class FriendConversation(isTyping: Boolean = false, messages: Map[Int, Message] = Map[Int, Message](),
    fileSentRecord: Map[Int, File] = Map[Int, File]())

  final case class Friend(
    userProfile: UserProfile = UserProfile(),
    userStatus: UserStatus,
    connectionStatus: ConnectionStatus,
    conversation: FriendConversation = FriendConversation(),
    publicKey: PublicKey = PublicKey()
  )

  final case class File(status: String)

  final case class Message(messageType: MessageType, timeDelta: Int, content: Array[Byte], messageStatus: MessageStatus)

  sealed abstract class MessageStatus
  final case class Sent() extends MessageStatus
  final case class Read() extends MessageStatus
  final case class Received() extends MessageStatus

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

  final case class ConnectionOptions(enableIPv6: Boolean, enableUdp: Boolean,
    proxyOption: ProxyOption, saveDataOption: SaveDataOption)

  sealed abstract class ProxyOption
  final case class Http(proxyHost: String, proxyPort: Int) extends ProxyOption
  final case class Socks5(proxyHost: String, proxyPort: Int) extends ProxyOption
  final case class NoProxy() extends ProxyOption

  sealed abstract class SaveDataOption
  final case class NoSaveData() extends SaveDataOption
  final case class ToxSave(data: Array[Byte]) extends SaveDataOption

  final case class PublicKey(key: Array[Byte] = Array.ofDim[Byte](0))
  final case class Nickname(name: String = "")
}
