package im.tox.client.hlapi

object State {

  final case class ToxState(userProfile: UserProfile, connectionStatus: String, userStatus: String,
    groups: Map[Int, Group], friends: Map[Int, Friend])

  final case class UserProfile(nickname: String, statusMessage: String, photo: String)
  final case class GroupProfile(name: String, introduction: String, photo: String, members: Map[Int, UserProfile])

  final case class PrivateConversation(isMuted: String, isTyping: Boolean, messages: Map[Int, Message], fileSentRecord: Seq[File])
  final case class PublicConversation(isMuted: String, messages: Map[Int, Message])

  final case class Friend(userProfile: UserProfile, conversation: PrivateConversation,
    alias: String, isBlocked: String, isStarred: String, connectionStatus: String, userStatus: String)

  final case class Group(groupProfile: GroupProfile, conversation: PublicConversation,
    alias: String, isStarred: String, option: String)

  final case class File(status: String)

  final case class Message(messageType: String, timestamp: Int, content: String, status: String)
}
