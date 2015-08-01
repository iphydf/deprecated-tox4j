package im.tox.hlapi.state

object UserStatusState {
  sealed abstract class UserStatus
  final case class Online() extends UserStatus
  final case class Away() extends UserStatus
  final case class Busy() extends UserStatus
  final case class Offline() extends UserStatus
}
