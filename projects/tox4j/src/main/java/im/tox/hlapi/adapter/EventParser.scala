package im.tox.hlapi.adapter

import im.tox.hlapi.entity.Action
import im.tox.hlapi.entity.Action._
import im.tox.hlapi.entity.Event._
import im.tox.hlapi.entity.CoreState._

import scalaz._

object EventParser {

  def parseUiEvent(e: UiEvent): State[ToxState, Action] = {

    e match {
      case RegisterEventListener(eventListener) => State[ToxState, Action] {
        state => (state, RegisterEventListenerAction(eventListener))
      }
      case SetConnectionStatusEvent(status) => State[ToxState, Action] {
        state => (connectionStatusL.set(state, status), SetConnectionStatusAction(status))
      }
      case SetUserStatusEvent(status) => State[ToxState, Action] {
        state => (userStatusL.set(state, status), SetUserStatusAction(status))
      }
      case SetStatusMessageEvent(message) => State[ToxState, Action] {
        state => (stateStatusMessageL.set(state, message), SetStatusMessageAction(message))
      }
      case SetNicknameEvent(name) => State[ToxState, Action] {
        state => (stateNicknameL.set(state, name), SetNameAction(name))
      }
      case DeleteFriendEvent(friendNumber) => State[ToxState, Action] {
        state => (friendsL.set(state, friendsL.get(state) - friendNumber), deleteFriend(friendNumber))
      }
      case SendFriendMessageEvent(friendNumber, message) => State[ToxState, Action] {
        state => {
          val friend = friendsL.get(state)(friendNumber)
          ( friendEventHandler[Map[Int, Message]](friendNumber, state, friendMessagesL,
              friendMessagesL.get(friend)
                + ((friendMessagesL.get(friend).size, message)))
            , SendFriendMessageAction(friendNumber, message))
        }
      }
      case SendFriendRequestEvent(publicKey, requestMessage) => State[ToxState, Action] {
        state => (state, SendFriendRequestAction(publicKey, requestMessage))
      }
      case SendFileTransmissionRequestEvent(friendNumber, fileDescription) => State[ToxState, Action] {
        state => (state, SendFileTransmissionRequestAction(friendNumber, fileDescription))
      }
      case GetFriendList() => State[ToxState, Action] {
        state => (state, GetFriendListSelfAction())
      }
      case GetMessageList(friendNumber) => State[ToxState, Action] {
        state => (state, GetMessageListSelfAction(friendNumber))
      }
      case GetFileList(friendNumber) => State[ToxState, Action] {
        state => (state, GetFileSentListSelfAction(friendNumber))
      }
      case GetPublicKeyEvent() => State[ToxState, Action] {
        state => (state, GetPublicKeySelfAction())
      }
    }
  }

  def parseNetworkEvent(e: NetworkEvent): State[ToxState, Action] = {

    e match {
      case ReceiveSelfConnectionStatusEvent(status) => State[ToxState, Action] {
        state => (connectionStatusL.set(state, status), SetConnectionStatusAction(status))
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
        state => (friendEventHandler[ConnectionStatus](friendNumber, state, friendConnectionStatusL, status), NoAction())
      }
      case ReceiveFriendMessageEvent(friendNumber, messageType, timeDelta, content) => State[ToxState, Action] {
        state =>
          {
            val friend = friendsL.get(state)(friendNumber)
            (
              friendEventHandler[Map[Int, Message]](friendNumber, state, friendMessagesL,
                friendMessagesL.get(friend)
                  + ((friendMessagesL.get(friend).size, Message(messageType, timeDelta, content, MessageReceived())))),
                NoAction()
            )
          }
      }

      case ReceiveFriendNameEvent(friendNumber, name) => State[ToxState, Action] {
        state => (friendEventHandler[Array[Byte]](friendNumber, state, friendNameL, name), NoAction())
      }
      case ReceiveFriendRequestEvent() => State[ToxState, Action] {
        state => (state, NoAction())
      }
      case ReceiveFriendStatusEvent(friendNumber, status) => State[ToxState, Action] {
        state => (friendEventHandler[UserStatus](friendNumber, state, friendUserStatusL, status), NoAction())
      }
      case ReceiveFriendStatusMessageEvent(friendNumber, statusMessage) => State[ToxState, Action] {
        state => (friendEventHandler[Array[Byte]](friendNumber, state, friendStatusMessageL, statusMessage), NoAction())
      }
      case ReceiveFriendTypingEvent(friendNumber, isTyping) => State[ToxState, Action] {
        state => (friendEventHandler[Boolean](friendNumber, state, friendIsTypingL, isTyping), NoAction())
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
            (friendEventHandler[Map[Int, Message]](friendNumber, state, friendMessagesL,
              friendMessagesL.get(friend).updated(
                messageId,
                messageStatusL.set(friendMessagesL.get(friend)(messageId), MessageRead())
              )), NoAction())
          }
      }
    }
  }

  def parseSelfEvent(e: SelfEvent): State[ToxState, Action] = {

    e match {
      case AddToFriendList(friendNumber, friend) => State[ToxState, Action] {
        state => (friendsL.set(state, friendsL.get(state) + ((friendNumber, friend))), NoAction())
      }
      case GetSelfPublicKeyEvent() => State[ToxState, Action] {
        state => (state, GetSelfPublicKeyAction())
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

