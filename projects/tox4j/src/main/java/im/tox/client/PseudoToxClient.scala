package im.tox.client

import im.tox.client.hlapi.Event
import im.tox.client.hlapi.Event._
import im.tox.client.hlapi.State._

final class PseudoToxClient {

  def acceptEvent(state: ToxState, e: Event): ToxState = {
    e match {
      case e: NetworkEvent => handleNetworkEvent(state, e)
      case e: UiEvent      => handleUiEvent(state, e)
    }
  }

  private def handleNetworkEvent(state: ToxState, e: NetworkEvent): ToxState = {
    e match {

      case ReceiveSelfConnectionStatus(status)   => state.copy(connectionStatus = status)
      //  Receive file transmission control from friends
      case ReceiveFileTransmissionControl()      => state
      //  Receive file transmission request from friends
      case ReceiveFileTransmissionRequest()      => state
      //  Receive a chunk of file under transmission from friends
      case ReceiveFileChunk()                    => state
      //  A friend’s connection status changes (online/offline)
      case ReceiveFriendConnectionStatusChange() => state
      //  Receive a message from a friend
      case ReceiveFriendMessage(friendNumber, messageType, timeStamp, content) => state.copy(friends =
        state.friends.updated(
          friendNumber,
          state.friends.apply(friendNumber).copy(conversation =
            state.friends.apply(friendNumber).conversation.copy(messages =
              state.friends.apply(friendNumber).conversation.messages
                + ((0, Message("new id", messageType, timeStamp, content, "received")))))
        ))
      //  A friend’s name changes
      case ReceiveFriendName(friendNumber: Int, name: String) => state.copy(friends =
        state.friends.updated(
          friendNumber,
          state.friends.apply(friendNumber).copy(userProfile =
            state.friends.apply(friendNumber).userProfile.copy(nickname = name))
        ))
      //  Receive a friend request
      case ReceiveFriendRequest()                    => state
      //  A friend’s user status changes
      case ReceiveFriendStatus(friendNumber, status) => state
      //  A friend’s status message changes
      case ReceiveFriendStatusMessageChange()        => state
      //  A friend typing status changes
      case ReceiveFriendTyping(friendNumber, isTyping) => state.copy(friends =
        state.friends.updated(
          friendNumber,
          state.friends.apply(friendNumber).copy(conversation =
            state.friends.apply(friendNumber).conversation.copy(isTyping = isTyping))
        ))
      //  A lossy packet arrives
      case ReceiveLossyPacket()    => state
      //  A lossless packet arrives
      case ReceiveLosslessPacket() => state
      //  Receive the read receipt of a message
      case ReceiveReadReceipt(friendNumber, messageId) => state.copy(friends =
        state.friends.updated(
          friendNumber,
          state.friends.apply(friendNumber).copy(conversation =
            state.friends.apply(friendNumber).conversation.copy(messages =
              state.friends.apply(friendNumber).conversation.messages.updated(
                messageId,
                state.friends.apply(friendNumber).conversation.messages.apply(messageId).copy(status = "read")
              )))
        ))
    }
  }

  private def handleUiEvent(state: ToxState, e: UiEvent): ToxState = {
    e match {

      /*
      Some of the cases only can be done after requests are approved by the other side,
      treated as already approve
       */
      case ChangeConnectionStatus(status)     => state.copy(connectionStatus = status)
      case ChangeUserStatus(status)           => state.copy(userStatus = status)
      case ChangeStatusMessage(statusMessage) => state.copy(userProfile = state.userProfile.copy(statusMessage = statusMessage))
      case ChangeNickname(nickname)           => state.copy(userProfile = state.userProfile.copy(nickname = nickname))
      //  Delete a friend
      case DeleteFriend(friendNumber)         => state.copy(friends = state.friends - friendNumber)
      //  Change a friend’s alias
      case ChangeFriendAlias(friendNumber, alias) => state.copy(friends =
        state.friends.updated(friendNumber, state.friends.apply(friendNumber).copy(alias = alias)))
      //  Change a group conversation’s alias
      case ChangeGroupAlias(groupNumber, alias) => state.copy(groups =
        state.groups.updated(groupNumber, state.groups.apply(groupNumber).copy(alias = alias)))
      //  Create a group chat
      case CreateGroup(groupName, option) => state.copy(groups = state.groups +
        ((0, Group(
          GroupProfile(groupName, "null", "null", Map[Int, UserProfile]()),
          PublicConversation("blah", Map[Int, Message]()),
          groupName, "none", option
        ))))
      //  Remove a member from a group chat
      case RemoveMemberFromGroupConversation(groupNumber, friendNumber) => state.copy(groups =
        state.groups.updated(
          groupNumber,
          state.groups.apply(groupNumber).copy(groupProfile =
            state.groups.apply(groupNumber).groupProfile.copy(members =
              state.groups.apply(groupNumber).groupProfile.members - friendNumber))
        ))
      //  Send a text message to a group
      case SendPrivateMessage(friendNumber, message) => state.copy(friends =
        state.friends.updated(
          friendNumber,
          state.friends.apply(friendNumber).copy(conversation =
            state.friends.apply(friendNumber).conversation.copy(messages =
              state.friends.apply(friendNumber).conversation.messages + ((0, message))))
        ))
      //  Send a text message to a group conversation
      case SendPublicMessage(groupNumber, message) => state.copy(groups =
        state.groups.updated(
          groupNumber,
          state.groups.apply(groupNumber).copy(conversation =
            state.groups.apply(groupNumber).conversation.copy(messages =
              state.groups.apply(groupNumber).conversation.messages + ((0, message))))

        ))
      //  Star/unstar a friend
      case ChangeFriendStarStatus(friendNumber) => state.copy(friends =
        state.friends.updated(
          friendNumber,
          state.friends.apply(friendNumber).copy(isStarred = "Star/unstar")
        ))
      // Star/unstar a group
      case ChangeGroupStarStatus(groupNumber) => state.copy(groups =
        state.groups.updated(
          groupNumber,
          state.groups.apply(groupNumber).copy(isStarred = "Star/unstar")
        ))
      //  Block/unblock a friend
      case ChangeFriendBlockStatus(friendNumber) => state.copy(friends =
        state.friends.updated(
          friendNumber,
          state.friends.apply(friendNumber).copy(isBlocked = "block/unblock")
        ))
      //  Mute/unmute a private conversation
      case ChangeFriendConversationMuteStatus(friendNumber) => state.copy(friends =
        state.friends.updated(
          friendNumber,
          state.friends.apply(friendNumber).copy(conversation =
            state.friends.apply(friendNumber).conversation.copy(isMuted = "mute/unmute"))
        ))
      //  Mute/unmute a group conversation
      case ChangeGroupConversationMuteStatus(groupNumber) => state.copy(groups =
        state.groups.updated(
          groupNumber,
          state.groups.apply(groupNumber).copy(conversation =
            state.groups.apply(groupNumber).conversation.copy(isMuted = "mute/unmute"))
        ))

      case SendFriendRequest(friendId, request) => state.copy()
      //  See the details of a friend’s profile
      case RequestFriendProfile(friendId) => state
      //  Send a request to join a group
      case SendJoinGroupConversationRequest(groupId, request) => state
      //  Invite a friend to a group chat
      case InviteFriendToGroupRequest(groupId, friendId, request) => state
      //  Initiate a conversation with a friend
      case CreatePrivateConversation(friendId) => state
      //  Initiate a group conversation
      case CreateGroupConversation(groupId) => state
      //  Dismiss a group
      case DismissGroupConversation(groupId) => state
      //  Leave a group conversation
      case LeaveGroupConversation(groupId) => state
      //  Delete a conversation
      case DeleteConversation(conversationId) => state
      //  Initiate a file transmission request to a friend
      case SendFileTransmissionRequest(friendId, fileDescription) => state
      //  Get all conversations
      case GetConversationList() => state
      //  Get all friends
      case GetFriendList() => state
      //  Get the messages associated with a conversation
      case GetMessageList(conversationId) => state
      //  Get the file sent history with a friend
      case GetFileSentList(conversationId) => state
      //  Login
      case Login(username, password) => state
      // Logout
      case Logout() => state
    }
  }

}
