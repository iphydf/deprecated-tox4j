package im.tox.hlapi.adapter

import im.tox.hlapi.adapter.ToxAdapter.acceptEvent
import im.tox.hlapi.adapter.EventParser._
import im.tox.hlapi.entity.Action
import im.tox.hlapi.entity.Action._
import im.tox.hlapi.entity.CoreState._
import im.tox.hlapi.entity.Event.{ RequestSuccess, ReplyEvent, SetConnectionStatusEvent }
import im.tox.tox4j.core.enums.{ ToxUserStatus, ToxMessageType }
import im.tox.tox4j.core.options.ToxOptions
import im.tox.tox4j.impl.jni.ToxCoreImpl

import scalaz._

object NetworkActionPerformer {

  var tox: ToxCoreImpl[ToxState] = new ToxCoreImpl[ToxState](ToxOptions())

  def performNetworkAction(action: Action): State[ToxState, ReplyEvent] = State {

    state =>
      {
        action match {
          case SetNameAction(nickname) => {
            tox.setName(nickname)
            (state, RequestSuccess())
          }
          case SetStatusMessageAction(statusMessage) => {
            tox.setStatusMessage(statusMessage)
            (state, RequestSuccess())
          }
          case SetUserStatusAction(status) => {
            status match {
              case Online()  => tox.setStatus(ToxUserStatus.NONE)
              case Away()    => tox.setStatus(ToxUserStatus.AWAY)
              case Busy()    => tox.setStatus(ToxUserStatus.BUSY)
              case Offline() => acceptEvent(SetConnectionStatusEvent(Disconnect()))
            }
            (state, RequestSuccess())
          }
          case GetFriendPublicKeyAction(friendNumber) => {
            val publicKey = tox.getFriendPublicKey(friendNumber)
            (friendEventHandler[PublicKey](friendNumber, state, friendPublicKeyL, PublicKey(publicKey)), RequestSuccess())
          }
          case GetSelfPublicKeyAction() => {
            val publicKey = tox.getAddress
            (publicKeyL.set(state, PublicKey(publicKey)), RequestSuccess())
          }
          case RegisterEventListenerAction(eventListener) => {
            tox.callback(new ToxCoreListener(eventListener))
            (state, RequestSuccess())
          }
          case SetConnectionStatusAction(status) => {
            InitiateConnection.acceptConnectionAction(state, status)
            (state, RequestSuccess())
          }
          case SendFriendRequestAction(publicKey, request) => {
            val friendNumber = {
              request match {
                case Some(requestMessage) => {
                  tox.addFriend(publicKey.key, requestMessage.request)
                }
                case None => {
                  tox.addFriendNorequest(publicKey.key)
                }
              }
            }
            (state, RequestSuccess())
          }
          case deleteFriend(friendNumber) => {
            tox.deleteFriend(friendNumber)
            (state, RequestSuccess())
          }
          case SendFriendMessageAction(friendNumber, message) => {
            message.messageType match {
              case NormalMessage() => {
                tox.friendSendMessage(friendNumber, ToxMessageType.NORMAL, message.timeDelta, message.content)
              }
              case ActionMessage() => {
                tox.friendSendMessage(friendNumber, ToxMessageType.ACTION, message.timeDelta, message.content)
              }
            }
            (state, RequestSuccess())
          }

        }
      }
  }

}
