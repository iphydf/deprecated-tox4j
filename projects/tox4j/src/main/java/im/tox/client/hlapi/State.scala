package im.tox.client.hlapi

object State {

  final case class ToxState(userProfile: UserProfile, connectionStatus: String, userStatus: String,
    groups: Seq[Group], friends: Seq[Friend])

  final case class UserProfile(nickname: String, statusMessage: String, photo: String)
  final case class GroupProfile(name: String, introduction: String, photo: String, members: Seq[User])

  final case class PrivateConversation(isMuted: String, messages: Seq[Message], fileSentRecord: Seq[File])
  final case class PublicConversation(isMuted: String, messages: Seq[Message])

  final case class User(Id: String, UserProfile: String)

  final case class Friend(user: User, conversation: PrivateConversation, alias: String, isBlocked: String, isStarred: String)

  final case class Group(group: GroupProfile, conversation: PublicConversation,
    alias: String, isStarred: String, option: String)

  final case class File(status: String)

  final case class Message(content: String)
}
