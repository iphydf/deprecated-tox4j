package im.tox.hlapi.adapter

import im.tox.hlapi.adapter.ToxAdapter.acceptEvent
import im.tox.hlapi.adapter.EventParser._
import im.tox.hlapi.entity.Event.SetConnectionStatusEvent
import im.tox.hlapi.entity.{ Response, Action }
import im.tox.hlapi.entity.Action._
import im.tox.hlapi.entity.CoreState._
import im.tox.hlapi.entity.Response._
import im.tox.tox4j.core.enums.{ToxFileKind, ToxUserStatus, ToxMessageType}
import im.tox.tox4j.core.options.ToxOptions
import im.tox.tox4j.impl.jni.ToxCoreImpl

import scalaz._

object NetworkActionPerformer {

  var tox: ToxCoreImpl[ToxState] = new ToxCoreImpl[ToxState](ToxOptions())

  def performNetworkAction(action: Action): State[ToxState, Response] = State {

    state =>
      {
        action match {
          case SetNameAction(nickname) => {
            tox.setName(nickname)
            (state, Success())
          }
          case SetStatusMessageAction(statusMessage) => {
            tox.setStatusMessage(statusMessage)
            (state, Success())
          }
          case SetUserStatusAction(status) => {
            status match {
              case Online()  => tox.setStatus(ToxUserStatus.NONE)
              case Away()    => tox.setStatus(ToxUserStatus.AWAY)
              case Busy()    => tox.setStatus(ToxUserStatus.BUSY)
              case Offline() => acceptEvent(SetConnectionStatusEvent(Disconnect()))
            }
            (state, Success())
          }
          case GetFriendPublicKeyAction(friendNumber) => {
            val publicKey = tox.getFriendPublicKey(friendNumber)
            (friendEventHandler[PublicKey](friendNumber, state, friendPublicKeyL, PublicKey(publicKey)), Success())
          }
          case GetSelfPublicKeyAction() => {
            val publicKey = tox.getAddress
            (publicKeyL.set(state, PublicKey(publicKey)), Success())
          }
          case RegisterEventListenerAction(eventListener) => {
            tox.callback(new ToxCoreListener(eventListener))
            (state, Success())
          }
          case SetConnectionStatusAction(status) => {
            InitiateConnection.acceptConnectionAction(state, status)
            (state, Success())
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
            (state, Success())
          }
          case deleteFriend(friendNumber) => {
            tox.deleteFriend(friendNumber)
            (state, Success())
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
            (state, Success())
          }
          case SendFileTransmissionRequestAction(friendNumber, file) => {
            val fileKind = {
              file.fileKind match {
                case Data => ToxFileKind.DATA
                case Avatar => ToxFileKind.AVATAR
              }
            }
            val fileId = tox.fileSend(friendNumber, fileKind, file.fileData.length, Array.ofDim[Byte](0), file.fileData)
            val friend = friendsL.get(state)(friendNumber)
            (
            friendEventHandler[Map[Int, File]](friendNumber, state, friendFilesL,
            friendFilesL.get(friend) +
              ((fileId, fileFileStatusL.set(file, FileSent()))))
            , Success())
          }
        }
      }
  }

}
