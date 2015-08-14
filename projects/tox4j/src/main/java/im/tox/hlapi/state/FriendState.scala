package im.tox.hlapi.state

import im.tox.hlapi.state.ConnectionState.{ Disconnect, ConnectionStatus }
import im.tox.hlapi.state.ConversationState.FriendConversation
import im.tox.hlapi.state.CoreState._
import im.tox.hlapi.state.PublicKeyState.PublicKey
import im.tox.hlapi.state.UserProfileState.UserProfile
import im.tox.hlapi.state.UserStatusState.{ UserStatus, Offline }

import scalaz.Lens

object FriendState {
  final case class Friend(
    userProfile: UserProfile = UserProfile(),
    userStatus: UserStatus = Offline(),
    connectionStatus: ConnectionStatus = Disconnect(),
    conversation: FriendConversation = FriendConversation(),
    publicKey: PublicKey = PublicKey()
  )

  final case class FriendList(friends: Map[Int, Friend] = Map[Int, Friend]()) {
    def size: Int = friends.size
  }
  final case class FriendRequest(request: Array[Byte] = Array.ofDim[Byte](0), timeDelta: Int = 0)

  val friendListFriendsL = Lens.lensu[FriendList, Map[Int, Friend]](
    (a, value) => a.copy(friends = value),
    _.friends
  )

  val friendConversationL = Lens.lensu[Friend, FriendConversation](
    (a, value) => a.copy(conversation = value),
    _.conversation
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

  val friendFilesL = friendConversationL >=> ConversationState.conversationFileSentListL >=> FileState.fileListFilesL
  val friendIsTypingL = friendConversationL >=> ConversationState.conversationIsTypingL
  val friendNameL = friendProfileL >=> UserProfileState.nicknameL
  val friendStatusMessageL = friendProfileL >=> UserProfileState.statusMessageL
  val friendSentMessageListL = friendConversationL >=> ConversationState.ConversationSentMessageListL
  val friendSentMessagesL = friendSentMessageListL >=> MessageState.MessageListMessagesL
  val friendReceivedMessageListL = friendConversationL >=> ConversationState.ConversationReceivedMessageListL
  val friendReceivedMessagesL = friendReceivedMessageListL >=> MessageState.MessageListMessagesL

  def friendSetHandler[T](friendNumber: Int, state: ToxState,
    lens: Lens[Friend, T], attribute: T): ToxState = {
    val friend = friendsL.get(state)(friendNumber)
    friendsL.set(
      state,
      friendsL.get(state).updated(
        friendNumber,
        lens.set(friend, attribute)
      )
    )
  }

}
