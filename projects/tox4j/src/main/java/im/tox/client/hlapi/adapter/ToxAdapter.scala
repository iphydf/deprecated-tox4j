package im.tox.client.hlapi.adapter

import scalaz.State

import im.tox.client.hlapi.entity.{ CoreState, Action, Event }
import Event._
import Action.SelfAction
import Action.NetworkAction
import CoreState._
import EventParser._
import im.tox.client.hlapi.adapter.NetworkActionPerformer.performNetworkAction
import im.tox.client.hlapi.adapter.SelfActionPerformer.performSelfAction

object ToxAdapter {

  def acceptEvent(e: Event): State[ToxState, Option[Gettable]] = {
    val decision = parseEvent(e)
    decision.flatMap(parseAction)
  }

  def parseEvent(e: Event): State[ToxState, Option[Action]] = {
    e match {
      case e: NetworkEvent => parseNetworkEvent(e)
      case e: UiEvent      => parseUiEvent(e)
      case e: SelfEvent    => parseSelfEvent(e)
    }
  }

  def parseAction(action: Option[Action]): State[ToxState, Option[Gettable]] = {
    action match {
      case action: NetworkAction => performNetworkAction(action)
      case action: SelfAction    => performSelfAction(action)
    }
  }

}

