package im.tox.client

import im.tox.client.hlapi.Event
import im.tox.client.hlapi.Event.{ NetworkEvent, UiEvent }

abstract class PseudoToxClient {

  def acceptEvent(e: Event): Unit = e match {
    case e: NetworkEvent => handleNetworkEvent(e)
    case e: UiEvent => handleUiEvent(e)
  }

  protected def handleNetworkEvent(e: NetworkEvent): Unit
  protected def handleUiEvent(e: UiEvent):Unit

}
