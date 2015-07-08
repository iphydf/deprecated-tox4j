package im.tox.client.hlapi

import im.tox.tox4j.core.proto.Core.Connection

sealed trait Status

object Status {

  sealed abstract class ConnectionStatus extends Status
  sealed abstract class UserStatus extends Status

  final case class TCP() extends ConnectionStatus
  final case class UDP() extends ConnectionStatus
  final case class None() extends ConnectionStatus

  final case class Online() extends UserStatus
  final case class Away() extends UserStatus
  final case class Busy() extends UserStatus
  final case class Offline() extends UserStatus

  def changeStatus(connectionStatus: ConnectionStatus, userStatus: UserStatus): (ConnectionStatus, UserStatus) =
    connectionStatus match {
      case connectionStatus: None => (None(), Offline())
      case connectionStatus: _ => userStatus match {
        case userStatus: Offline => (None(), Offline())
        case userStatus: _ => (connectionStatus, userStatus)
    }
  }
}
