package im.tox.client.hlapi.adapter

import scalaz.State

import im.tox.client.hlapi.entity.{ CoreState, Action, Event }
import Event._
import CoreState._
import EventParser._
import im.tox.client.hlapi.adapter.ToxCoreIOPerformer.performAction

object ToxAdapter {

  def acceptEvent(e: Event): State[ToxState, Option[Gettable]] = {
    val decision = parseEvent(e)
    decision.flatMap(performAction)
  }

  def parseEvent(e: Event): State[ToxState, Option[Action]] = {
    e match {
      case e: NetworkEvent => parseNetworkEvent(e)
      case e: UiEvent      => parseUiEvent(e)
      case e: SelfEvent    => parseSelfEvent(e)
    }
  }

}

