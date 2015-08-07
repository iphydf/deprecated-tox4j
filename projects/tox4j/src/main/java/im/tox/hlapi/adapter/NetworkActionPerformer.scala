package im.tox.hlapi.adapter

import im.tox.hlapi.action.Action.NetworkActionType
import im.tox.hlapi.action.NetworkAction._
import im.tox.hlapi.adapter.EventParser._
import im.tox.hlapi.event.Event.UiEventType
import im.tox.hlapi.event.UiEvent.ToxEndEvent
import im.tox.hlapi.listener.{ ToxClientListener, ToxCoreListener }
import im.tox.hlapi.state.ConnectionState._
import im.tox.hlapi.state.CoreState.ToxState
import im.tox.hlapi.state.FileState.{ File, FileSent, Avatar, Data }
import im.tox.hlapi.state.FriendState.Friend
import im.tox.hlapi.state.MessageState.{ ActionMessage, NormalMessage }
import im.tox.hlapi.state.{ FileState, CoreState, FriendState }
import im.tox.hlapi.state.PublicKeyState.PublicKey
import im.tox.hlapi.state.UserStatusState.{ Offline, Busy, Away, Online }
import im.tox.tox4j.core.enums.{ ToxFileKind, ToxUserStatus, ToxMessageType }
import im.tox.tox4j.core.options.{ SaveDataOptions, ProxyOptions, ToxOptions }
import im.tox.tox4j.impl.jni.ToxCoreImpl

import scalaz._

object NetworkActionPerformer {

  def performNetworkAction(action: NetworkActionType, adapter: ToxAdapter): State[ToxState, Unit] = State {

    state =>
      {
        action.networkAction match {
          case SetNameAction(nickname) => {
            adapter.tox.setName(nickname)
            (state, Unit)
          }
          case SetStatusMessageAction(statusMessage) => {
            adapter.tox.setStatusMessage(statusMessage)
            (state, Unit)
          }
          case SetUserStatusAction(status) => {
            status match {
              case Online()  => adapter.tox.setStatus(ToxUserStatus.NONE)
              case Away()    => adapter.tox.setStatus(ToxUserStatus.AWAY)
              case Busy()    => adapter.tox.setStatus(ToxUserStatus.BUSY)
              case Offline() => adapter.acceptEvent(UiEventType(ToxEndEvent()))
            }
            (state, Unit)
          }
          case SendFriendRequestAction(publicKey, request) => {
            adapter.tox.addFriend(publicKey.key, request.request)
            (state, Unit)
          }
          case DeleteFriend(friendNumber) => {
            adapter.tox.deleteFriend(friendNumber)
            (state, Unit)
          }
          case SendFriendMessageAction(friendNumber, message) => {
            message.messageType match {
              case NormalMessage() => {
                adapter.tox.friendSendMessage(friendNumber, ToxMessageType.NORMAL, message.timeDelta, message.content)
              }
              case ActionMessage() => {
                adapter.tox.friendSendMessage(friendNumber, ToxMessageType.ACTION, message.timeDelta, message.content)
              }
            }
            (state, Unit)
          }
          case SendFileTransmissionRequestAction(friendNumber, file) => {
            val fileKind = {
              file.fileKind match {
                case Data()   => ToxFileKind.DATA
                case Avatar() => ToxFileKind.AVATAR
              }
            }
            val fileId = adapter.tox.fileSend(friendNumber, fileKind, file.fileData.length, Array.ofDim[Byte](0), file.fileData)
            val friend = CoreState.friendsL.get(state)(friendNumber)
            (
              friendEventHandler[Map[Int, File]](friendNumber, state, FriendState.friendFilesL,
                FriendState.friendFilesL.get(friend) +
                  ((fileId, FileState.fileFileStatusL.set(file, FileSent())))), Unit
            )
          }
          case ToxInitAction(connectionOptions, toxClientListener) => {
            (initConnection(adapter, state, connectionOptions, toxClientListener), Unit)
          }
          case ToxEndAction() => {
            adapter.eventLoop.interrupt()
            adapter.tox.close()
            adapter.eventLoop.join()
            (state, Unit)
          }
          case AddFriendNoRequestAction(publicKey) => {
            val friendNumber = adapter.tox.addFriendNorequest(publicKey.key)
            (CoreState.friendsL.set(
              state,
              CoreState.friendsL.get(state) + ((friendNumber, Friend(publicKey = publicKey)))
            ), Unit)
          }
        }
      }
  }
  def initConnection(adapter: ToxAdapter, state: ToxState, connectionOptions: ConnectionOptions, toxClientListener: ToxClientListener): ToxState = {
    val toxOption: ToxOptions = {
      val p = connectionOptions.proxyOption
      val proxy = {
        p match {
          case p: Http    => ProxyOptions.Http(p.proxyHost, p.proxyPort)
          case p: Socks5  => ProxyOptions.Socks5(p.proxyHost, p.proxyPort)
          case p: NoProxy => ProxyOptions.None
        }
      }
      val s = connectionOptions.saveDataOption
      val saveData = s match {
        case s: NoSaveData => SaveDataOptions.None
        case s: ToxSave    => SaveDataOptions.ToxSave(s.data)
      }
      ToxOptions(connectionOptions.enableIPv6, connectionOptions.enableUdp, proxy, saveData = saveData)
    }
    adapter.tox = new ToxCoreImpl[ToxState](toxOption)
    adapter.isInit = true
    adapter.tox.callback(new ToxCoreListener(toxClientListener, adapter))
    adapter.eventLoop = new Thread(new Runnable() {
      override def run(): Unit = {
        mainLoop(adapter.state)
      }
      def mainLoop(toxState: ToxState): ToxState = {
        Thread.sleep(adapter.tox.iterationInterval)
        adapter.state = adapter.tox.iterate(adapter.state)
        mainLoop(adapter.state)
      }
    })
    adapter.eventLoop.start()
    var returnState = CoreState.publicKeyL.set(state, PublicKey(adapter.tox.getPublicKey))
    returnState = CoreState.stateNicknameL.set(returnState, adapter.tox.getName)
    returnState = CoreState.stateStatusMessageL.set(returnState, adapter.tox.getStatusMessage)
    val friendList = adapter.tox.getFriendList
    if (friendList.length != 0) {
      for (i <- 0 to friendList.length) {
        val publicKey = adapter.tox.getFriendPublicKey(friendList(i))
        returnState = CoreState.friendsL.set(
          state,
          CoreState.friendsL.get(returnState) + ((friendList(i), Friend(publicKey = PublicKey(publicKey))))
        )
      }
    }
    returnState
  }
}
