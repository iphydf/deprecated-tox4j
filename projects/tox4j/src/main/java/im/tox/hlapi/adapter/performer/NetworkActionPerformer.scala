package im.tox.hlapi.performer

import im.tox.hlapi.action.NetworkAction
import im.tox.hlapi.action.NetworkAction._
import im.tox.hlapi.adapter.ToxAdapter
import im.tox.hlapi.event.NetworkEvent
import im.tox.hlapi.event.UiEvent.ToxEndEvent
import im.tox.hlapi.listener.{ ToxClientListener, ToxCoreListener }
import im.tox.hlapi.state.ConnectionState._
import im.tox.hlapi.state.CoreState.ToxState
import im.tox.hlapi.state.FileState.{ Avatar, Data, File, FileSent }
import im.tox.hlapi.state.FriendState.Friend
import im.tox.hlapi.state.MessageState.{ ActionMessage, NormalMessage }
import im.tox.hlapi.state.PublicKeyState.{ Address, PublicKey }
import im.tox.hlapi.state.UserStatusState.{ Away, Busy, Offline, Online }
import im.tox.hlapi.state.{ CoreState, FileState, FriendState }
import im.tox.tox4j.core.enums.{ ToxFileKind, ToxMessageType, ToxUserStatus }
import im.tox.tox4j.core.options.{ ProxyOptions, SaveDataOptions, ToxOptions }
import im.tox.tox4j.impl.jni.ToxCoreImpl

import scala.collection.immutable.Queue

object NetworkActionPerformer {

  def perform(action: NetworkAction, tox: ToxCoreImpl[Queue[NetworkEvent]], state: ToxState): ToxState = {
    action match {
      case SetNameAction(nickname) => {
        tox.setName(nickname)
        state
      }
      case SetStatusMessageAction(statusMessage) => {
        tox.setStatusMessage(statusMessage)
        state
      }
      case SetUserStatusAction(status) => {
        status match {
          case Online()  => tox.setStatus(ToxUserStatus.NONE)
          case Away()    => tox.setStatus(ToxUserStatus.AWAY)
          case Busy()    => tox.setStatus(ToxUserStatus.BUSY)
          case Offline() =>
        }
        state
      }
      case SendFriendRequestAction(address, request) => {
        val friendNumber = tox.addFriend(address.address, request.request)
        (CoreState.friendsL.set(
          state,
          CoreState.friendsL.get(state) + ((friendNumber, Friend()))
        ))
      }
      case DeleteFriend(friendNumber) => {
        tox.deleteFriend(friendNumber)
        state
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
        state
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
        FriendState.friendEventHandler[Map[Int, File]](friendNumber, state, FriendState.friendFilesL,
          FriendState.friendFilesL.get(friend) +
            ((fileId, FileState.fileFileStatusL.set(file, FileSent()))))
      }

      case ToxEndAction() => {
        state
      }
      case AddFriendNoRequestAction(publicKey) => {
        val friendNumber = tox.addFriendNorequest(publicKey.key)
        (CoreState.friendsL.set(
          state,
          CoreState.friendsL.get(state) + ((friendNumber, Friend(publicKey = publicKey)))
        ))
      }
    }
  }
  def initConnection(action: ToxInitAction): (ToxCoreImpl[Queue[NetworkEvent]], ToxState) = {
    val toxOption: ToxOptions = {
      val p = action.connectionOptions.proxyOption
      val proxy = {
        p match {
          case p: Http    => ProxyOptions.Http(p.proxyHost, p.proxyPort)
          case p: Socks5  => ProxyOptions.Socks5(p.proxyHost, p.proxyPort)
          case p: NoProxy => ProxyOptions.None
        }
      }
      val s = action.connectionOptions.saveDataOption
      val saveData = s match {
        case s: NoSaveData => SaveDataOptions.None
        case s: ToxSave    => SaveDataOptions.ToxSave(s.data)
      }
      ToxOptions(action.connectionOptions.enableIPv6, action.connectionOptions.enableUdp, proxy, saveData = saveData)
    }
    val tox = new ToxCoreImpl[Queue[NetworkEvent]](toxOption)
    tox.callback(ToxCoreListener)
    var returnState = CoreState.publicKeyL.set(ToxState(), PublicKey(tox.getPublicKey))
    returnState = CoreState.addressL.set(returnState, Address(tox.getAddress))
    returnState = CoreState.stateNicknameL.set(returnState, tox.getName)
    returnState = CoreState.stateStatusMessageL.set(returnState, tox.getStatusMessage)
    val friendList = tox.getFriendList
    if (friendList.length != 0) {
      for (i <- 0 to friendList.length) {
        val publicKey = tox.getFriendPublicKey(friendList(i))
        returnState = CoreState.friendsL.set(
          returnState,
          CoreState.friendsL.get(returnState) + ((friendList(i), Friend(publicKey = PublicKey(publicKey))))
        )
      }
    }
    (tox, returnState)
  }
}
