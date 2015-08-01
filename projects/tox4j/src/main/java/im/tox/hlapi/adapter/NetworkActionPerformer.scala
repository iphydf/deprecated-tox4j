package im.tox.hlapi.adapter

import im.tox.hlapi.action.Action
import im.tox.hlapi.action.Action.NetworkActionType
import im.tox.hlapi.action.NetworkAction._
import im.tox.hlapi.adapter.ToxAdapter.acceptEvent
import im.tox.hlapi.adapter.EventParser._
import im.tox.hlapi.event.Event.UiEventType
import im.tox.hlapi.event.UiEvent.SetConnectionStatusEvent
import im.tox.hlapi.listener.ToxCoreListener
import im.tox.hlapi.response.Response
import im.tox.hlapi.response.Response.RequestSuccessResponse
import im.tox.hlapi.response.SuccessResponse.RequestSuccess
import im.tox.hlapi.state.ConnectionState.Disconnect
import im.tox.hlapi.state.CoreState.ToxState
import im.tox.hlapi.state.FileState.{ File, FileSent, Avatar, Data }
import im.tox.hlapi.state.MessageState.{ ActionMessage, NormalMessage }
import im.tox.hlapi.state.{ FileState, CoreState, FriendState }
import im.tox.hlapi.state.PublicKeyState.PublicKey
import im.tox.hlapi.state.UserStatusState.{ Offline, Busy, Away, Online }
import im.tox.tox4j.core.enums.{ ToxFileKind, ToxUserStatus, ToxMessageType }
import im.tox.tox4j.core.options.ToxOptions
import im.tox.tox4j.impl.jni.ToxCoreImpl

import scalaz._

object NetworkActionPerformer {

  var tox: ToxCoreImpl[ToxState] = new ToxCoreImpl[ToxState](ToxOptions())

  def performNetworkAction(action: NetworkActionType): State[ToxState, Response] = State {

    state =>
      {
        action.networkAction match {
          case SetNameAction(nickname) => {
            tox.setName(nickname)
            (state, RequestSuccessResponse(RequestSuccess()))
          }
          case SetStatusMessageAction(statusMessage) => {
            tox.setStatusMessage(statusMessage)
            (state, RequestSuccessResponse(RequestSuccess()))
          }
          case SetUserStatusAction(status) => {
            status match {
              case Online()  => tox.setStatus(ToxUserStatus.NONE)
              case Away()    => tox.setStatus(ToxUserStatus.AWAY)
              case Busy()    => tox.setStatus(ToxUserStatus.BUSY)
              case Offline() => acceptEvent(UiEventType(SetConnectionStatusEvent(Disconnect())))
            }
            (state, RequestSuccessResponse(RequestSuccess()))
          }
          case GetFriendPublicKeyAction(friendNumber) => {
            val publicKey = tox.getFriendPublicKey(friendNumber)
            (friendEventHandler[PublicKey](friendNumber, state, FriendState.friendPublicKeyL, PublicKey(publicKey)), RequestSuccessResponse(RequestSuccess()))
          }
          case GetSelfPublicKeyAction() => {
            val publicKey = tox.getAddress
            (CoreState.publicKeyL.set(state, PublicKey(publicKey)), RequestSuccessResponse(RequestSuccess()))
          }
          case RegisterEventListenerAction(eventListener) => {
            tox.callback(new ToxCoreListener(eventListener))
            (state, RequestSuccessResponse(RequestSuccess()))
          }
          case SetConnectionStatusAction(status) => {
            InitiateConnection.acceptConnectionAction(state, status)
            (state, RequestSuccessResponse(RequestSuccess()))
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
            (state, RequestSuccessResponse(RequestSuccess()))
          }
          case deleteFriend(friendNumber) => {
            tox.deleteFriend(friendNumber)
            (state, RequestSuccessResponse(RequestSuccess()))
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
            (state, RequestSuccessResponse(RequestSuccess()))
          }
          case SendFileTransmissionRequestAction(friendNumber, file) => {
            val fileKind = {
              file.fileKind match {
                case Data()   => ToxFileKind.DATA
                case Avatar() => ToxFileKind.AVATAR
              }
            }
            val fileId = tox.fileSend(friendNumber, fileKind, file.fileData.length, Array.ofDim[Byte](0), file.fileData)
            val friend = CoreState.friendsL.get(state)(friendNumber)
            (
              friendEventHandler[Map[Int, File]](friendNumber, state, FriendState.friendFilesL,
                FriendState.friendFilesL.get(friend) +
                  ((fileId, FileState.fileFileStatusL.set(file, FileSent())))), RequestSuccessResponse(RequestSuccess())
            )
          }
        }
      }
  }

}
