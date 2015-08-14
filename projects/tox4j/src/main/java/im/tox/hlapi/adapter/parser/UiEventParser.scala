package im.tox.hlapi.adapter.parser

import im.tox.hlapi.action.NetworkAction
import im.tox.hlapi.action.NetworkAction._
import im.tox.hlapi.event.UiEvent
import im.tox.hlapi.event.UiEvent._
import im.tox.hlapi.state.ConnectionState.{ Connect, Disconnect }
import im.tox.hlapi.state.CoreState._
import im.tox.hlapi.state.FriendState
import im.tox.hlapi.state.MessageState._

object UiEventParser extends EventParser {

  def parse(state: ToxState, e: UiEvent): (ToxState, Option[NetworkAction]) = {

    e match {
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
        (state, Some(SendFriendMessageAction(friendNumber, message)))
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
      case SetTypingEvent(friendNumber, isTyping) => {
        (state, Some(SetTypingAction(friendNumber, isTyping)))
      }
    }
  }

}

