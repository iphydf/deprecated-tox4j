package im.tox.hlapi

import com.typesafe.scalalogging.Logger
import im.tox.client.{Friend, ToxClientState}
import im.tox.hlapi.Client.ToxClientConnection
import im.tox.tox4j.core.data._
import im.tox.tox4j.core.enums.{ToxConnection, ToxUserStatus}
import org.slf4j.LoggerFactory

import scalaz.stream._

/**
 * Handles friend requests and friend information updates like nickname and status message.
 */
case object FriendListEventListener extends EventProcessor[ToxClientState] {

  private val logger = Logger(LoggerFactory.getLogger(getClass))

  private def updateFriend(friendNumber: ToxFriendNumber, state: ToxClientState)(update: Friend => Friend): Process[ToxClientConnection, ToxClientState] = {
    val updated = update(state.friends.getOrElse(friendNumber, Friend()))
    Process.emit(state.copy(friends = state.friends + (friendNumber -> updated)))
  }

  override def friendStatus(friendNumber: ToxFriendNumber, status: ToxUserStatus)(state: ToxClientState): Process[ToxClientConnection, ToxClientState] = {
    updateFriend(friendNumber, state)(_.copy(status = status))
  }

  override def friendTyping(friendNumber: ToxFriendNumber, isTyping: Boolean)(state: ToxClientState): Process[ToxClientConnection, ToxClientState] = {
    updateFriend(friendNumber, state)(_.copy(typing = isTyping))
  }

  override def friendName(friendNumber: ToxFriendNumber, name: ToxNickname)(state: ToxClientState): Process[ToxClientConnection, ToxClientState] = {
    updateFriend(friendNumber, state)(_.copy(name = name))
  }

  override def friendStatusMessage(friendNumber: ToxFriendNumber, message: ToxStatusMessage)(state: ToxClientState): Process[ToxClientConnection, ToxClientState] = {
    updateFriend(friendNumber, state)(_.copy(statusMessage = message))
  }

  override def friendConnectionStatus(friendNumber: ToxFriendNumber, connectionStatus: ToxConnection)(state: ToxClientState): Process[ToxClientConnection, ToxClientState] = {
    updateFriend(friendNumber, state)(_.copy(connection = connectionStatus))
  }

  override def friendRequest(publicKey: ToxPublicKey, timeDelta: Int, message: ToxFriendRequestMessage)(state: ToxClientState): Process[ToxClientConnection, ToxClientState] = {
    for {
      _ <- Client.privateAccess { (tox, av) =>
        logger.info(s"Adding $publicKey as friend")
        tox.addFriendNorequest(publicKey)
      }
    } yield {
      state.copy(
        profile = state.profile.addFriendKeys(publicKey.toHexString)
      )
    }
  }

}
