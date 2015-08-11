package im.tox.hlapi.adapter.parser

import im.tox.hlapi.action.NetworkAction
import im.tox.hlapi.action.NetworkAction._
import im.tox.hlapi.event.UiEvent
import im.tox.hlapi.event.UiEvent._
import im.tox.hlapi.state.ConnectionState.{Connect, Disconnect}
import im.tox.hlapi.state.CoreState._
import im.tox.hlapi.state.FriendState
import im.tox.hlapi.state.MessageState._


object UiEventParser extends EventParser {

  def parse(state: ToxState, e: UiEvent): (ToxState, Option[NetworkAction]) = {

    e match {
      case ToxInitEvent(connectionOptions, eventListener) => {
        (connectionStatusL.set(state, Connect(connectionOptions)), Some(ToxInitAction(connectionOptions, eventListener)))
      }
      case ToxEndEvent() => {
        (connectionStatusL.set(state, Disconnect()), Some(ToxEndAction()))
      }
      case SetUserStatusEvent(status) => {
        (userStatusL.set(state, status), Some(SetUserStatusAction(status)))
      }
      case SetStatusMessageEvent(message) => {
        (stateStatusMessageL.set(state, message), Some(SetStatusMessageAction(message)))
      }
      case SetNicknameEvent(name) => {
        (stateNicknameL.set(state, name), Some(SetNameAction(name)))
      }
      case DeleteFriendEvent(friendNumber) => {
        (friendsL.set(state, friendsL.get(state) - friendNumber), Some(DeleteFriend(friendNumber)))
      }
      case SendFriendMessageEvent(friendNumber, messageContent) => {
        val message = Message(NormalMessage(), 0, messageContent, MessageSent())
        val friend = friendsL.get(state)(friendNumber)
        (friendEventHandler[Map[Int, Message]](friendNumber, state, FriendState.friendMessagesL,
          FriendState.friendMessagesL.get(friend)
            + ((FriendState.friendMessagesL.get(friend).size, message))), Some(SendFriendMessageAction(friendNumber, message)))
      }
      case SendFriendRequestEvent(address, requestMessage) => {
        (state, Some(SendFriendRequestAction(address, requestMessage)))
      }
      case SendFileTransmissionRequestEvent(friendNumber, fileDescription) => {
        (state, Some(SendFileTransmissionRequestAction(friendNumber, fileDescription)))
      }
      case AddFriendNoRequestEvent(publicKey) => {
        (state, Some(AddFriendNoRequestAction(publicKey)))
      }
    }
  }

}

