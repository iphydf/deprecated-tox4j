package im.tox.client.hlapi

object State {

  final case class ToxState(userProfile: UserProfile, connectionStatus: String, userStatus: String,
    conversations: Seq[Conversation], friends: Seq[Friend])

  final case class UserProfile(nickName: String, statusMessage: String, photo: String)

  final case class Conversation()

  final case class Friend()

}
