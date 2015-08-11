package im.tox.hlapi.adapter

import im.tox.hlapi.action.Action
import Action._
import im.tox.hlapi.action.NetworkAction._
import im.tox.hlapi.event.Event
import Event._
import im.tox.hlapi.event.NetworkEvent._
import im.tox.hlapi.event.UiEvent._
import im.tox.hlapi.state.ConnectionState.{ Disconnect, Connect, ConnectionStatus }
import im.tox.hlapi.state.CoreState._
import im.tox.hlapi.state.{ CoreState, MessageState, FriendState }
import im.tox.hlapi.state.FriendState.Friend
import im.tox.hlapi.state.MessageState._
import im.tox.hlapi.state.UserStatusState.UserStatus

import scalaz._

object EventParser {

  def parseUiEvent(e: UiEventType, state: ToxState): (ToxState, Action) = {

    e.uiEvent match {
      case ToxInitEvent(connectionOptions, eventListener) => {
        (connectionStatusL.set(state, Connect(connectionOptions)), NetworkActionType(ToxInitAction(connectionOptions, eventListener)))
      }
      case ToxEndEvent() => {
        (connectionStatusL.set(state, Disconnect()), NetworkActionType(ToxEndAction()))
      }
      case SetUserStatusEvent(status) => {
        (userStatusL.set(state, status), NetworkActionType(SetUserStatusAction(status)))
      }
      case SetStatusMessageEvent(message) => {
        (stateStatusMessageL.set(state, message), NetworkActionType(SetStatusMessageAction(message)))
      }
      case SetNicknameEvent(name) => {
        (stateNicknameL.set(state, name), NetworkActionType(SetNameAction(name)))
      }
      case DeleteFriendEvent(friendNumber) => {
        (friendsL.set(state, friendsL.get(state) - friendNumber), NetworkActionType(DeleteFriend(friendNumber)))
      }
      case SendFriendMessageEvent(friendNumber, messageContent) => {
        val message = Message(NormalMessage(), 0, messageContent, MessageSent())
        val friend = friendsL.get(state)(friendNumber)
        (friendEventHandler[Map[Int, Message]](friendNumber, state, FriendState.friendMessagesL,
          FriendState.friendMessagesL.get(friend)
            + ((FriendState.friendMessagesL.get(friend).size, message))), NetworkActionType(SendFriendMessageAction(friendNumber, message)))
      }
      case SendFriendRequestEvent(address, requestMessage) => {
        (state, NetworkActionType(SendFriendRequestAction(address, requestMessage)))
      }
      case SendFileTransmissionRequestEvent(friendNumber, fileDescription) => {
        (state, NetworkActionType(SendFileTransmissionRequestAction(friendNumber, fileDescription)))
      }
      case AddFriendNoRequestEvent(publicKey) => {
        (state, NetworkActionType(AddFriendNoRequestAction(publicKey)))
      }
    }
  }

  def parseNetworkEvent(e: NetworkEventType, state: ToxState): (ToxState, Action) = {

    e.networkEvent match {
      case ReceiveSelfConnectionStatusEvent(status) => {
        (connectionStatusL.set(state, status), NoAction())
      }
      case ReceiveFileTransmissionControlEvent() => {
        (state, NoAction())
      }
      case ReceiveFileTransmissionRequestEvent() => {
        (state, NoAction())
      }
      case ReceiveFileChunkEvent() => {
        (state, NoAction())
      }
      case ReceiveFriendConnectionStatusEvent(friendNumber, status) => {
        var nextState = state
        if (!friendExist(friendNumber, state)) {
          nextState = CoreState.friendsL.set(
            nextState,
            CoreState.friendsL.get(nextState) + ((friendNumber, Friend()))
          )
        }
        (friendEventHandler[ConnectionStatus](friendNumber, nextState, FriendState.friendConnectionStatusL, status), NoAction())
      }
      case ReceiveFriendMessageEvent(friendNumber, messageType, timeDelta, content) => {

        val friend = friendsL.get(state)(friendNumber)
        (
          friendEventHandler[Map[Int, Message]](friendNumber, state, FriendState.friendMessagesL,
            FriendState.friendMessagesL.get(friend)
              + ((FriendState.friendMessagesL.get(friend).size, Message(messageType, timeDelta, content, MessageReceived())))),
            NoAction()
        )
      }

      case ReceiveFriendNameEvent(friendNumber, name) => {
        (friendEventHandler[Array[Byte]](friendNumber, state, FriendState.friendNameL, name), NoAction())
      }
      case ReceiveFriendRequestEvent() => {
        (state, NoAction())
      }
      case ReceiveFriendStatusEvent(friendNumber, status) => {
        (friendEventHandler[UserStatus](friendNumber, state, FriendState.friendUserStatusL, status), NoAction())
      }
      case ReceiveFriendStatusMessageEvent(friendNumber, statusMessage) => {
        (friendEventHandler[Array[Byte]](friendNumber, state, FriendState.friendStatusMessageL, statusMessage), NoAction())

      }
      case ReceiveFriendTypingEvent(friendNumber, isTyping) => {

        (friendEventHandler[Boolean](friendNumber, state, FriendState.friendIsTypingL, isTyping), NoAction())

      }
      case ReceiveLossyPacketEvent() => {
        (state, NoAction())
      }
      case ReceiveLosslessPacketEvent() => {
        (state, NoAction())
      }
      case ReceiveFriendReadReceiptEvent(friendNumber, messageId) => {
        val friend = friendsL.get(state)(friendNumber)
        (friendEventHandler[Map[Int, Message]](friendNumber, state, FriendState.friendMessagesL,
          FriendState.friendMessagesL.get(friend).updated(
            messageId,
            MessageState.messageStatusL.set(FriendState.friendMessagesL.get(friend)(messageId), MessageRead())
          )), NoAction())
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

  private def friendExist(friendNumber: Int, state: ToxState): Boolean = {
    val friends = CoreState.friendsL.get(state)
    friends.contains(friendNumber)
  }

}

