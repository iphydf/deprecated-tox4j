package im.tox.hlapi.action

sealed trait Action

object Action {

  final case class NetworkActionType(networkAction: NetworkAction) extends Action
  final case class DiskIOActionType(diskIOAction: DiskIOAction) extends Action
  final case class NoAction() extends Action

}
