package im.tox.hlapi.adapter

import im.tox.hlapi.action.Action
import Action._
import im.tox.hlapi.action.NetworkAction._
import im.tox.hlapi.action.SelfAction.{ GetPublicKeySelfAction, GetFileSentListSelfAction, GetMessageListSelfAction, GetFriendListSelfAction }
import im.tox.hlapi.event.Event
import Event._
import im.tox.hlapi.event.NetworkEvent._
import im.tox.hlapi.event.SelfEvent.{ GetSelfPublicKeyEvent, AddToFriendList }
import im.tox.hlapi.event.UiEvent._
import im.tox.hlapi.state.ConnectionState.{ Disconnect, Connect, ConnectionStatus }
import im.tox.hlapi.state.CoreState._
import im.tox.hlapi.state.{ MessageState, FriendState }
import im.tox.hlapi.state.FriendState.Friend
import im.tox.hlapi.state.MessageState.{ MessageRead, MessageReceived, Message }
import im.tox.hlapi.state.UserStatusState.UserStatus

import scalaz._

object EventParser {

  def parseUiEvent(e: UiEventType): State[ToxState, Action] = {

    e.uiEvent match {
      case ToxInitEvent(connectionOptions, eventListener) => State[ToxState, Action] {
        state => (connectionStatusL.set(state, Connect(connectionOptions)), NetworkActionType(ToxInitAction(connectionOptions, eventListener)))
      }
      case ToxEndEvent() => State[ToxState, Action] {
        state => (connectionStatusL.set(state, Disconnect()), NetworkActionType(ToxEndAction()))
      }
      case SetUserStatusEvent(status) => State[ToxState, Action] {
        state => (userStatusL.set(state, status), NetworkActionType(SetUserStatusAction(status)))
      }
      case SetStatusMessageEvent(message) => State[ToxState, Action] {
        state => (stateStatusMessageL.set(state, message), NetworkActionType(SetStatusMessageAction(message)))
      }
      case SetNicknameEvent(name) => State[ToxState, Action] {
        state => (stateNicknameL.set(state, name), NetworkActionType(SetNameAction(name)))
      }
      case DeleteFriendEvent(friendNumber) => State[ToxState, Action] {
        state => (friendsL.set(state, friendsL.get(state) - friendNumber), NetworkActionType(deleteFriend(friendNumber)))
      }
      case SendFriendMessageEvent(friendNumber, message) => State[ToxState, Action] {
        state =>
          {
            val friend = friendsL.get(state)(friendNumber)
            (friendEventHandler[Map[Int, Message]](friendNumber, state, FriendState.friendMessagesL,
              FriendState.friendMessagesL.get(friend)
                + ((FriendState.friendMessagesL.get(friend).size, message))), NetworkActionType(SendFriendMessageAction(friendNumber, message)))
          }
      }
      case SendFriendRequestEvent(publicKey, requestMessage) => State[ToxState, Action] {
        state => (state, NetworkActionType(SendFriendRequestAction(publicKey, requestMessage)))
      }
      case SendFileTransmissionRequestEvent(friendNumber, fileDescription) => State[ToxState, Action] {
        state => (state, NetworkActionType(SendFileTransmissionRequestAction(friendNumber, fileDescription)))
      }
      case GetFriendList() => State[ToxState, Action] {
        state => (state, SelfActionType(GetFriendListSelfAction()))
      }
      case GetMessageList(friendNumber) => State[ToxState, Action] {
        state => (state, SelfActionType(GetMessageListSelfAction(friendNumber)))
      }
      case GetFileList(friendNumber) => State[ToxState, Action] {
        state => (state, SelfActionType(GetFileSentListSelfAction(friendNumber)))
      }
      case GetPublicKeyEvent() => State[ToxState, Action] {
        state => (state, SelfActionType(GetPublicKeySelfAction()))
      }
    }
  }

  def parseNetworkEvent(e: NetworkEventType): State[ToxState, Action] = {

    e.networkEvent match {
      case ReceiveSelfConnectionStatusEvent(status) => State[ToxState, Action] {
        state => (connectionStatusL.set(state, status), NoAction())
      }
      case ReceiveFileTransmissionControlEvent() => State[ToxState, Action] {
        state => (state, NoAction())
      }
      case ReceiveFileTransmissionRequestEvent() => State[ToxState, Action] {
        state => (state, NoAction())
      }
      case ReceiveFileChunkEvent() => State[ToxState, Action] {
        state => (state, NoAction())
      }
      case ReceiveFriendConnectionStatusEvent(friendNumber, status) => State[ToxState, Action] {
        state => (friendEventHandler[ConnectionStatus](friendNumber, state, FriendState.friendConnectionStatusL, status), NoAction())
      }
      case ReceiveFriendMessageEvent(friendNumber, messageType, timeDelta, content) => State[ToxState, Action] {
        state =>
          {
            val friend = friendsL.get(state)(friendNumber)
            (
              friendEventHandler[Map[Int, Message]](friendNumber, state, FriendState.friendMessagesL,
                FriendState.friendMessagesL.get(friend)
                  + ((FriendState.friendMessagesL.get(friend).size, Message(messageType, timeDelta, content, MessageReceived())))),
                NoAction()
            )
          }
      }

      case ReceiveFriendNameEvent(friendNumber, name) => State[ToxState, Action] {
        state => (friendEventHandler[Array[Byte]](friendNumber, state, FriendState.friendNameL, name), NoAction())
      }
      case ReceiveFriendRequestEvent() => State[ToxState, Action] {
        state => (state, NoAction())
      }
      case ReceiveFriendStatusEvent(friendNumber, status) => State[ToxState, Action] {
        state => (friendEventHandler[UserStatus](friendNumber, state, FriendState.friendUserStatusL, status), NoAction())
      }
      case ReceiveFriendStatusMessageEvent(friendNumber, statusMessage) => State[ToxState, Action] {
        state => (friendEventHandler[Array[Byte]](friendNumber, state, FriendState.friendStatusMessageL, statusMessage), NoAction())
      }
      case ReceiveFriendTypingEvent(friendNumber, isTyping) => State[ToxState, Action] {
        state => (friendEventHandler[Boolean](friendNumber, state, FriendState.friendIsTypingL, isTyping), NoAction())
      }
      case ReceiveLossyPacketEvent() => State[ToxState, Action] {
        state => (state, NoAction())
      }
      case ReceiveLosslessPacketEvent() => State[ToxState, Action] {
        state => (state, NoAction())
      }
      case ReceiveFriendReadReceiptEvent(friendNumber, messageId) => State[ToxState, Action] {
        state =>
          {
            val friend = friendsL.get(state)(friendNumber)
            (friendEventHandler[Map[Int, Message]](friendNumber, state, FriendState.friendMessagesL,
              FriendState.friendMessagesL.get(friend).updated(
                messageId,
                MessageState.messageStatusL.set(FriendState.friendMessagesL.get(friend)(messageId), MessageRead())
              )), NoAction())
          }
      }
    }
  }

  def parseSelfEvent(e: SelfEventType): State[ToxState, Action] = {

    e.selfEvent match {
      case AddToFriendList(friendNumber, friend) => State[ToxState, Action] {
        state => (friendsL.set(state, friendsL.get(state) + ((friendNumber, friend))), NoAction())
      }
      case GetSelfPublicKeyEvent() => State[ToxState, Action] {
        state => (state, NetworkActionType(GetSelfPublicKeyAction()))
      }
    }
  }

  def friendEventHandler[T](friendNumber: Int, state: ToxState,
    lens: Lens[Friend, T], attribute: T): ToxState = {
    val friend = friendsL.get(state)(friendNumber)
    friendsL.set(
      state,
      friendsL.get(state).updated(
        friendNumber,
        lens.set(friend, attribute)
      )
    )
  }
}

