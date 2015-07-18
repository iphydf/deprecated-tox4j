package im.tox.client.hlapi.adapter

import im.tox.client.hlapi.entity.Action
import im.tox.client.hlapi.entity.Action._
import im.tox.client.hlapi.entity.Event._
import im.tox.client.hlapi.entity.CoreState._

import scalaz._

object EventParser {

  def parseUiEvent(e: UiEvent): State[ToxState, Option[Action]] = {
    e match {
      case RegisterEventListener(eventListener) => State[ToxState, Option[Action]] {
        state => (state, Some(RegisterEventListenerAction(eventListener)))
      }
      case SetConnectionStatusEvent(status) => State[ToxState, Option[Action]] {
        state => (connectionStatusL.set(state, status), Some(SetConnectionStatusAction(status)))
      }
      case SetUserStatusEvent(status) => State[ToxState, Option[Action]] {
        state => (userStatusL.set(state, status), Some(SetUserStatusAction(status)))
      }
      case SetStatusMessageEvent(message) => State[ToxState, Option[Action]] {
        state => (stateStatusMessageL.set(state, message), Some(SetStatusMessageAction(message)))
      }
      case SetNicknameEvent(name) => State[ToxState, Option[Action]] {
        state => (stateNicknameL.set(state, name), Some(SetNameAction(name)))
      }
      case DeleteFriendEvent(friendNumber) => State[ToxState, Option[Action]] {
        state => (friendsL.set(state, friendsL.get(state) - friendNumber), Some(deleteFriend(friendNumber)))
      }
      case SendFriendMessageEvent(friendNumber, message) => State[ToxState, Option[Action]] {
        state => (state, Some(SendFriendMessageAction(friendNumber, message)))
      }
      case SendFriendRequestEvent(publicKey, requestMessage) => State[ToxState, Option[Action]] {
        state => (state, Some(SendFriendRequestAction(publicKey, requestMessage)))
      }
      case SendFileTransmissionRequest(friendId, fileDescription) => State[ToxState, Option[Action]] {
        state => (state, None)
      }
      case GetFriendList() => State[ToxState, Option[Action]] {
        state => (state, Some(GetFriendListSelfAction()))
      }
      case GetMessageList(friendNumber) => State[ToxState, Option[Action]] {
        state => (state, Some(GetMessageListSelfAction(friendNumber)))
      }
      case GetFileSentList(friendNumber) => State[ToxState, Option[Action]] {
        state => (state, Some(GetFileSentListSelfAction(friendNumber)))
      }
    }
  }

  def parseNetworkEvent(e: NetworkEvent): State[ToxState, Option[Action]] = {

    e match {
      case ReceiveSelfConnectionStatus(status) => State[ToxState, Option[Action]] {
        state => (connectionStatusL.set(state, status), Some(SetConnectionStatusAction(status)))
      }
      case ReceiveFileTransmissionControl() => State[ToxState, Option[Action]] {
        state => (state, None)
      }
      case ReceiveFileTransmissionRequest() => State[ToxState, Option[Action]] {
        state => (state, None)
      }
      case ReceiveFileChunk() => State[ToxState, Option[Action]] {
        state => (state, None)
      }
      case ReceiveFriendConnectionStatus(friendNumber, status) => State[ToxState, Option[Action]] {
        state => (friendEventHandler[ConnectionStatus](friendNumber, state, friendConnectionStatusL, status), None)
      }
      case ReceiveFriendMessage(friendNumber, messageType, timeDelta, content) => State[ToxState, Option[Action]] {
        state =>
          {
            val friend = friendsL.get(state)(friendNumber)
            (
              friendEventHandler[Map[Int, Message]](friendNumber, state, friendMessagesL,
                friendMessagesL.get(friend)
                  + ((friendMessagesL.get(friend).size, Message(messageType, timeDelta, content, Received())))),
                None
            )
          }
      }

      case ReceiveFriendName(friendNumber, name) => State[ToxState, Option[Action]] {
        state => (friendEventHandler[String](friendNumber, state, friendNameL, name.toString), None)
      }
      case ReceiveFriendRequest() => State[ToxState, Option[Action]] {
        state => (state, None)
      }
      case ReceiveFriendStatus(friendNumber, status) => State[ToxState, Option[Action]] {
        state => (friendEventHandler[UserStatus](friendNumber, state, friendUserStatusL, status), None)
      }
      case ReceiveFriendStatusMessageChange() => State[ToxState, Option[Action]] {
        state => (state, None)
      }
      case ReceiveFriendTyping(friendNumber, isTyping) => State[ToxState, Option[Action]] {
        state => (friendEventHandler[Boolean](friendNumber, state, friendIsTypingL, isTyping), None)
      }
      case ReceiveLossyPacket() => State[ToxState, Option[Action]] {
        state => (state, None)
      }
      case ReceiveLosslessPacket() => State[ToxState, Option[Action]] {
        state => (state, None)
      }
      case ReceiveReadReceipt(friendNumber, messageId) => State[ToxState, Option[Action]] {
        state =>
          {
            val friend = friendsL.get(state)(friendNumber)
            (friendEventHandler[Map[Int, Message]](friendNumber, state, friendMessagesL,
              friendMessagesL.get(friend).updated(
                messageId,
                messageStatusL.set(friendMessagesL.get(friend)(messageId), Read())
              )), None)
          }
      }
    }
  }

  def parseSelfEvent(e: SelfEvent): State[ToxState, Option[Action]] = {

    e match {

      case AddToFriendList(friendNumber, friend) => State[ToxState, Option[Action]] {
        state => (friendsL.set(state, friendsL.get(state) + ((friendNumber, friend))), None)
      }
      case GetSelfPublicKeyEvent() => State[ToxState, Option[Action]] {
        state => (state, Some(GetSelfPublicKeyAction()))
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

