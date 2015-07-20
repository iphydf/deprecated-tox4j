package im.tox.hlapi.adapter

import scalaz.State

import im.tox.hlapi.entity.{ CoreState, Action, Event }
import Event._
import Action.SelfAction
import Action.NetworkAction
import CoreState._
import EventParser._
import im.tox.hlapi.adapter.NetworkActionPerformer.performNetworkAction
import im.tox.hlapi.adapter.SelfActionPerformer.performSelfAction

object ToxAdapter {

  val state: ToxState = ToxState()

  def acceptEvent(e: Event): ReplyEvent = {
    val decision = parseEvent(e)
    decision.flatMap(parseAction).eval(state)
  }

  def parseEvent(e: Event): State[ToxState, Action] = {
    e match {
      case e: NetworkEvent => parseNetworkEvent(e)
      case e: UiEvent      => parseUiEvent(e)
      case e: SelfEvent    => parseSelfEvent(e)
    }
  }

  def parseAction(action: Action): State[ToxState, ReplyEvent] = {
    action match {
      case action: NetworkAction => performNetworkAction(action)
      case action: SelfAction    => performSelfAction(action)
    }
  }

}

