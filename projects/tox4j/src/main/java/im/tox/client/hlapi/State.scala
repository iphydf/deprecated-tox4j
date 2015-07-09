package im.tox.client.hlapi

object State {

  final case class ToxState(userProfile: UserProfile, connectionStatus: String, userStatus: String,
    groups: Seq[Group], friends: Seq[Friend])

  final case class UserProfile(nickname: String, statusMessage: String, photo: String)
  final case class GroupProfile(name: String, introduction: String, photo: String, members: Seq[User])

  final case class PrivateConversation(isMuted: String, isTyping: Boolean, messages: Seq[Message], fileSentRecord: Seq[File])
  final case class PublicConversation(isMuted: String, messages: Seq[Message])

  final case class User(Number: Int, userProfile: UserProfile)

  final case class Friend(user: User, conversation: PrivateConversation, status: String,
    alias: String, isBlocked: String, isStarred: String)

  final case class Group(group: GroupProfile, conversation: PublicConversation,
    alias: String, isStarred: String, option: String)

  final case class File(status: String)

  final case class Message(Id: String, messageType: String, timestamp: Int, content: String, status: String)
}
