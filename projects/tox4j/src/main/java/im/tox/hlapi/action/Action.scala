package im.tox.hlapi.action

sealed trait Action

object Action {

  final case class NetworkActionType(networkAction: NetworkAction) extends Action
  final case class SelfActionType(selfAction: SelfAction) extends Action
  final case class NoAction() extends Action

}
