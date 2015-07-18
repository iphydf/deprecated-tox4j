package im.tox.client.hlapi.adapter

import im.tox.client.hlapi.adapter.EventParser._
import im.tox.client.hlapi.entity.Action
import im.tox.client.hlapi.entity.Action._
import im.tox.client.hlapi.entity.CoreState._
import im.tox.tox4j.core.enums.ToxMessageType
import im.tox.tox4j.core.options.ToxOptions
import im.tox.tox4j.impl.jni.ToxCoreImpl

import scalaz._

object ToxCoreIOPerformer {

  var tox: ToxCoreImpl[Unit] = new ToxCoreImpl[Unit](ToxOptions())

  def performAction(action: Option[Action]): State[ToxState, Option[Gettable]] = State {
    state =>
      {
        action match {
          case SetNameAction(nickname) => {
            tox.setName(nickname.getBytes)
            (state, None)
          }
          case SetStatusMessageAction(statusMessage) => {
            tox.setStatusMessage(statusMessage.getBytes)
            (state, None)
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

}
