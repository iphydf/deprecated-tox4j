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

  /**
   * Get the corresponding friend
   * @param state
   * @param friendNumber
   * @return
   */
  private def getFriend(state: ToxState, friendNumber: Int): Friend = {
    state.friends.filter(p => friendNumber == friendNumber).apply(0)
  }

  private def getMessageWithFriend(state: ToxState, friendNumber: Int, messageId: Int): Message = {
    getFriend(state, friendNumber).conversation.messages.filter(p => messageId == p.Id).apply(0)
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
          state.friends.indexOf(getFriend(state, friendNumber)),
          getFriend(state, friendNumber).copy(conversation =
            getFriend(state, friendNumber).conversation.copy(messages =
              getFriend(state, friendNumber).conversation.messages
                :+ Message("new id", messageType, timeStamp, content, "received")))
        ))
      //  A friend’s name changes
      case ReceiveFriendName(friendNumber: Int, name: String) => state.copy(friends =
        state.friends.updated(
          state.friends.indexOf(getFriend(state, friendNumber)),
          getFriend(state, friendNumber).copy(user =
            getFriend(state, friendNumber).user.copy(userProfile =
              getFriend(state, friendNumber).user.userProfile.copy(nickname = name)))
        ))
      //  Receive a friend request
      case ReceiveFriendRequest()             => state
      //  A friend’s user status changes
      case ReceiveFriendUserStatusChange()    => state
      //  A friend’s status message changes
      case ReceiveFriendStatusMessageChange() => state
      //  A friend typing status changes
      case ReceiveFriendTyping(friendNumber, isTyping) => state.copy(friends =
        state.friends.updated(
          state.friends.indexOf(getFriend(state, friendNumber)),
          getFriend(state, friendNumber).copy(conversation =
            getFriend(state, friendNumber).conversation.copy(isTyping = isTyping))
        ))
      //  A lossy packet arrives
      case ReceiveLossyPacket()    => state
      //  A lossless packet arrives
      case ReceiveLosslessPacket() => state
      //  Receive the read receipt of a message
      case ReceiveReadReceipt(friendNumber, messageId) => state.copy(friends =
        state.friends.updated(
          state.friends.indexOf(getFriend(state, friendNumber)),
          getFriend(state, friendNumber).copy(conversation =
            getFriend(state, friendNumber).conversation.copy(messages =
              getFriend(state, friendNumber).conversation.messages.updated(
                getFriend(state, friendNumber).conversation.messages.indexOf(
                  getMessageWithFriend(state, friendNumber, messageId)
                ),
                getMessageWithFriend(state, friendNumber, messageId).copy(status = "read")
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
      case DeleteFriend(friend)               => state.copy(friends = state.friends.filter(p => p == friend))
      //  Change a friend’s alias
      case ChangeFriendAlias(friend, alias) => state.copy(friends =
        state.friends.updated(state.friends.indexOf(friend), friend.copy(alias = alias)))
      //  Change a group conversation’s alias
      case ChangeGroupAlias(group, alias) => state.copy(groups =
        state.groups.updated(state.groups.indexOf(group), group.copy(alias = alias)))
      //  Create a group chat
      case CreateGroup(groupName, option) => state.copy(groups = state.groups :+
        Group(
          GroupProfile(groupName, "null", "null", Seq[User]()),
          PublicConversation("blah", Seq[Message]()),
          groupName, "none", option
        ))
      //  Remove a member from a group chat
      case RemoveMemberFromGroupConversation(group, user) => state.copy(groups =
        state.groups.updated(
          state.groups.indexOf(group),
          group.copy(group = group.group.copy(
            members = group.group.members.filter(p => p == group)
          ))
        ))
      //  Send a text message to a group
      case SendPrivateMessage(friend, message) => state.copy(friends =
        state.friends.updated(
          state.friends.indexOf(friend),
          friend.copy(conversation = friend.conversation.copy(
            messages = friend.conversation.messages :+ message
          ))
        ))
      //  Send a text message to a group conversation
      case SendPublicMessage(group, message) => state.copy(groups =
        state.groups.updated(
          state.groups.indexOf(group),
          group.copy(conversation = group.conversation.copy(
            messages = group.conversation.messages :+ message
          ))
        ))
      //  Star/unstar a friend
      case ChangeFriendStarStatus(friend) => state.copy(friends =
        state.friends.updated(
          state.friends.indexOf(friend),
          friend.copy(isStarred = "Star/unstar")
        ))
      // Star/unstar a group
      case ChangeGroupStarStatus(group) => state.copy(groups =
        state.groups.updated(
          state.groups.indexOf(group),
          group.copy(isStarred = "Star/unstar")
        ))
      //  Block/unblock a friend
      case ChangeFriendBlockStatus(friend) => state.copy(friends =
        state.friends.updated(
          state.friends.indexOf(friend),
          friend.copy(isBlocked = "block/unblock")
        ))
      //  Mute/unmute a private conversation
      case ChangeFriendConversationMuteStatus(friend) => state.copy(friends =
        state.friends.updated(
          state.friends.indexOf(friend),
          friend.copy(conversation = friend.conversation.copy(isMuted = "Mute/Unmute"))
        ))
      //  Mute/unmute a group conversation
      case ChangeGroupConversationMuteStatus(group) => state.copy(groups =
        state.groups.updated(
          state.groups.indexOf(group),
          group.copy(conversation = group.conversation.copy(isMuted = "Mute/Unmute"))
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
