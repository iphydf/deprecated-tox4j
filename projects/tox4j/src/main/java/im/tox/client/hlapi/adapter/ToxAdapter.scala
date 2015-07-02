package im.tox.client.hlapi.adapter

import scalaz.State

import im.tox.client.hlapi.entity.{ CoreState, Action, Event }
import Action._
import Event._
import CoreState._
import EventParser._
import im.tox.tox4j.core.enums.ToxMessageType
import im.tox.tox4j.core.options.ToxOptions
import im.tox.tox4j.impl.jni.ToxCoreImpl

object ToxAdapter {

  var tox: ToxCoreImpl[Unit] = new ToxCoreImpl[Unit](ToxOptions())

  def acceptEvent(e: Event): State[ToxState, Any] = {
    val decision = parseEvent(e)
    decision.flatMap(_ => State[ToxState, Any] {
      state => {
        _ match {
          case SetNameAction(nickname) => {
            tox.setName(nickname.getBytes)
            (state, Unit)
          }
          case SetStatusMessageAction(statusMessage) => {
            tox.setStatusMessage(statusMessage.getBytes)
            (state, Unit)
          }
          case SetUserStatusAction(status) => (state, Unit)
          case GetFriendPublicKeyAction(friendNumber) => {
            val publicKey = tox.getFriendPublicKey(friendNumber)
            (friendEventHandler[PublicKey](friendNumber, state, friendPublicKeyL, PublicKey(publicKey)), Unit)
          }
          case GetSelfPublicKeyAction() => {
            val publicKey = tox.getAddress
            (publicKeyL.set(state, PublicKey(publicKey)), Unit)
          }
          case RegisterEventListenerAction(eventListener) => {
            tox.callback(eventListener)
            (state, Unit)
          }
          case SetConnectionStatusAction(status) => {
            InitiateConnection.acceptConnectionAction(state, status)
            (state, Unit)
          }
          case SendFriendRequestAction(publicKey, request) => {
            val friendNumber = {
              request match {
                case Some(request: Array[Byte]) => {
                  tox.addFriend(publicKey, request)
                }
                case None => {
                  tox.addFriendNorequest(publicKey)
                }
              }
            }
            (state, Unit)
          }
          case deleteFriend(friendNumber) => (state, Unit)
          case SendFriendMessageAction(friendNumber, message) => {
            message.messageType match {
              case NormalMessage() => {
                tox.friendSendMessage(friendNumber, ToxMessageType.NORMAL, message.timeDelta, message.content)
              }
              case ActionMessage() => {
                tox.friendSendMessage(friendNumber, ToxMessageType.ACTION, message.timeDelta, message.content)
              }
            }
            (state, Unit)
        }
      }
    }
  }

  def parseEvent(e: Event): State[ToxState, Option[Action]] = {
    e match {
      case e: NetworkEvent => parseNetworkEvent(e)
      case e: UiEvent      => parseUiEvent(e)
      case e: SelfEvent => parseSelfEvent(e)
    }
  }

}
