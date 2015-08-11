package im.tox.hlapi.adapter

import im.tox.hlapi.action.NetworkAction
import im.tox.hlapi.adapter.parser.{UiEventParser, RequestParser, NetworkEventParser}
import im.tox.hlapi.adapter.performer.{DiskIOActionPerformer, NetworkActionPerformer}
import im.tox.hlapi.event.{UiEvent, NetworkEvent}
import im.tox.hlapi.listener.ToxClientListener
import im.tox.hlapi.request.{ Reply, Request }
import im.tox.hlapi.state.CoreState.ToxState
import im.tox.tox4j.impl.jni.ToxCoreImpl

import UiEventParser._
import RequestParser._

@SuppressWarnings(Array("org.brianmckenna.wartremover.warts.Null"))
final class ToxAdapter(toxClientListener: ToxClientListener) {

  var state: ToxState = ToxState()
  var tox: ToxCoreImpl[Seq[NetworkEvent]] = null

  def acceptUiEvent(e: UiEvent): Unit = {
    val decision = UiEventParser.parse(state, e)
    state = {
      decision._2 match {
        case Some(action) => NetworkActionPerformer.perform(action, tox, state)
        case None => state
      }
    }
  }

  def acceptNetworkEvent(e: NetworkEvent): Unit = {
    val decision = NetworkEventParser.parse(state, e)
    state = {
      decision._2 match {
        case Some(action) => DiskIOActionPerformer.perform(state)
        case None => state
      }
    }

  }

  def acceptRequest(request: Request): Reply = {
    parseRequest(request, state)
  }

}

