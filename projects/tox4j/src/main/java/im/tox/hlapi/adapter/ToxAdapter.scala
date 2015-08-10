package im.tox.hlapi.adapter

import im.tox.hlapi.action.Action.{ NoAction, NetworkActionType }
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

@SuppressWarnings(Array("org.brianmckenna.wartremover.warts.Null"))
final class ToxAdapter {

  var state: ToxState = ToxState()
  var tox: ToxCoreImpl[ToxState] = null
  var isInit: Boolean = false
  var eventLoop: Thread = new Thread()

  def acceptEvent(e: Event): Unit = {
    val decision = parseEvent(e, state)
    state = parseAction(decision._2, decision._1)
  }

  def acceptRequest(request: Request): Reply = {
    parseRequest(request, state)
  }

  def parseEvent(e: Event, state: ToxState): (ToxState, Action) = {
    e match {
      case e: NetworkEventType => parseNetworkEvent(e, state)
      case e: UiEventType      => parseUiEvent(e, state)
    }
  }

  def parseAction(action: Action, state: ToxState): ToxState = {
    action match {
      case networkAction: NetworkActionType => performNetworkAction(networkAction, this, state)
      case NoAction()                       => state
    }
  }

}

