package im.tox.hlapi.adapter

import im.tox.hlapi.action.Action.{ SelfActionType, NetworkActionType }
import im.tox.hlapi.action.{ SelfAction, NetworkAction, Action }
import im.tox.hlapi.event.Event
import im.tox.hlapi.response.Response
import im.tox.hlapi.state.CoreState.ToxState

import scalaz.State

import Event._
import EventParser._
import im.tox.hlapi.adapter.NetworkActionPerformer.performNetworkAction
import im.tox.hlapi.adapter.SelfActionPerformer.performSelfAction

object ToxAdapter {

  val state: ToxState = ToxState()

  def acceptEvent(e: Event): Response = {
    val decision = parseEvent(e)
    decision.flatMap(parseAction).eval(state)
  }

  def parseEvent(e: Event): State[ToxState, Action] = {
    e match {
      case e: NetworkEventType => parseNetworkEvent(e)
      case e: UiEventType      => parseUiEvent(e)
      case e: SelfEventType    => parseSelfEvent(e)
    }
  }

  def parseAction(action: Action): State[ToxState, Response] = {
    action match {
      case networkAction: NetworkActionType => performNetworkAction(networkAction)
      case selfAction: SelfActionType       => performSelfAction(selfAction)
    }
  }

}

