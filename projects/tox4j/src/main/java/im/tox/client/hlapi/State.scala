package im.tox.client.hlapi

object State {

  final case class ToxState(userProfile: UserProfile, status: Status, conversations: Conversations, friends: Friends)

  final case class UserProfile(nickName: String, statusMessage: String, photo: String)

  final case class Status(connectionStatus: String, userStatus: String)

  final case class Conversations()

  final case class Friends()

}
