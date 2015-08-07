package im.tox.hlapi.event

sealed trait Event

object Event {
  final case class NetworkEventType(networkEvent: NetworkEvent) extends Event
  final case class UiEventType(uiEvent: UiEvent) extends Event
}

