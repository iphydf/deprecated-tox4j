package im.tox.client.hlapi.adapter

import im.tox.client.hlapi.adapter.ToxAdapter.acceptEvent
import im.tox.client.hlapi.adapter.EventParser._
import im.tox.client.hlapi.entity.Action
import im.tox.client.hlapi.entity.Action._
import im.tox.client.hlapi.entity.CoreState._
import im.tox.client.hlapi.entity.Event.SetConnectionStatusEvent
import im.tox.tox4j.core.enums.{ ToxUserStatus, ToxMessageType }
import im.tox.tox4j.core.options.ToxOptions
import im.tox.tox4j.impl.jni.ToxCoreImpl

import scalaz._

object NetworkActionPerformer {

  var tox: ToxCoreImpl[Unit] = new ToxCoreImpl[Unit](ToxOptions())

  def performNetworkAction(action: Option[Action]): State[ToxState, Option[Gettable]] = State {

    state =>
      {
        action match {
          case Some(SetNameAction(nickname)) => {
            tox.setName(nickname)
            (state, None)
          }
          case Some(SetStatusMessageAction(statusMessage)) => {
            tox.setStatusMessage(statusMessage)
            (state, None)
          }
          case Some(SetUserStatusAction(status)) => {
            status match {
              case Online()  => tox.setStatus(ToxUserStatus.NONE)
              case Away()    => tox.setStatus(ToxUserStatus.AWAY)
              case Busy()    => tox.setStatus(ToxUserStatus.BUSY)
              case Offline() => acceptEvent(SetConnectionStatusEvent(Disconnect()))
            }
            (state, None)
          }
          case Some(GetFriendPublicKeyAction(friendNumber)) => {
            val publicKey = tox.getFriendPublicKey(friendNumber)
            (friendEventHandler[PublicKey](friendNumber, state, friendPublicKeyL, PublicKey(publicKey)), None)
          }
          case Some(GetSelfPublicKeyAction()) => {
            val publicKey = tox.getAddress
            (publicKeyL.set(state, PublicKey(publicKey)), None)
          }
          case Some(RegisterEventListenerAction(eventListener)) => {
            tox.callback(new ToxCoreListener[Unit](eventListener))
            (state, None)
          }
          case Some(SetConnectionStatusAction(status)) => {
            InitiateConnection.acceptConnectionAction(state, status)
            (state, None)
          }
          case Some(SendFriendRequestAction(publicKey, request)) => {
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
            (state, None)
          }
          case Some(deleteFriend(friendNumber)) => {
            tox.deleteFriend(friendNumber)
            (state, None)
          }
          case Some(SendFriendMessageAction(friendNumber, message)) => {
            message.messageType match {
              case NormalMessage() => {
                tox.friendSendMessage(friendNumber, ToxMessageType.NORMAL, message.timeDelta, message.content)
              }
              case ActionMessage() => {
                tox.friendSendMessage(friendNumber, ToxMessageType.ACTION, message.timeDelta, message.content)
              }
            }
            (state, None)
          }

        }
      }
  }

}
