package im.tox.hlapi.adapter

import im.tox.hlapi.action.Action.NetworkActionType
import im.tox.hlapi.action.Action
import im.tox.hlapi.event.Event
import im.tox.hlapi.request.{ Reply, Request }
import im.tox.hlapi.state.CoreState.ToxState
import im.tox.tox4j.core.options.ToxOptions
import im.tox.tox4j.impl.jni.ToxCoreImpl

import scalaz.State

import Event._
import EventParser._
import RequestParser._
import im.tox.hlapi.adapter.NetworkActionPerformer.performNetworkAction

final class ToxAdapter {

  var state: ToxState = ToxState()
  var tox: ToxCoreImpl[ToxState] = new ToxCoreImpl[ToxState](ToxOptions())
  var isInit: Boolean = false
  var eventLoop: Thread = new Thread()

  def acceptEvent(e: Event): Unit = {
    parseEvent(e)
  }

  def acceptRequest(request: Request): Reply = {
    parseRequest(request, state)
  }

  def parseEvent(e: Event): State[ToxState, Action] = {
    e match {
      case e: NetworkEventType => parseNetworkEvent(e)
      case e: UiEventType      => parseUiEvent(e)
    }
  }

  def parseAction(action: Action): State[ToxState, Unit] = {
    action match {
      case networkAction: NetworkActionType => performNetworkAction(networkAction, this)
    }
  }

}

