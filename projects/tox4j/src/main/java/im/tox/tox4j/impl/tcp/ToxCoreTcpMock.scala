package im.tox.tox4j.impl.tcp

import java.io.{ IOException, InputStream, OutputStream }
import java.net.Socket
import java.nio.ByteBuffer
import scala.annotation.tailrec
import scala.collection.immutable.Queue
import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory

import im.tox.tox4j.core.{ ToxCoreConstants, AbstractToxCore }
import im.tox.tox4j.core.options.ToxOptions
import im.tox.tox4j.core.callbacks._
import im.tox.tox4j.core.enums._
import im.tox.tox4j.core.exceptions._
import im.tox.tox4j.impl.tcp.messages._
import im.tox.tox4j.impl.tcp.ToxCoreExceptionRaiser._

final class ToxCoreTcpMock extends AbstractToxCore {
  private val UPDATE_INTERVAL = 50
  private val CHECKSUM_SIZE = 2

  private val logger = Logger(LoggerFactory.getLogger(this.getClass))

  // scalastyle:ignore
  private var lastUpdateTime: Long = 0

  // scalastyle:ignore
  private var state: ToxCoreState = ToxCoreState(FriendState(
    PublicKey.random(), ToxConnection.NONE, ToxUserStatus.NONE, Array.ofDim(0), Array.ofDim(0), false, 0
  ), NoSpam(0), false, None, Queue.empty[NetworkMessage], Map.empty[Int, FriendState], None, new CallbacksState)

  private def createChecksum(data: Array[Byte]): Array[Byte] = {
    val checksum = Array.ofDim[Byte](CHECKSUM_SIZE)
    for (i <- data.indices) {
      checksum(i % CHECKSUM_SIZE) = (checksum(i % CHECKSUM_SIZE) ^ data(i)).toByte
    }
    checksum
  }

  /**
   * Searches for a friendNumber that is free.
   */
  private def getFreeFriendNumber: Int = {
    // We create a list with (state.friends.size + 1) distinct elements and then
    // remove state.friends.size elements from it (or less, if friends contains the
    // same element more than once). This ensures that we can call .head and won't fail.
    (0 to state.friends.size).filter(
      !state.friends.contains(_)
    ).head
  }

  /**
   * Finds the number of a friend by a given public key.
   */
  private def findFriendNumber(key: PublicKey): Option[Int] = {
    state.friends.find(_._2.publicKey == key).map(_._1)
  }

  private def sendMessages(): Unit = {
    state.connection.foreach(con => {
      state.messages.foreach(con.write(_))
      state = state.copy(messages = Queue.empty[NetworkMessage])
    })
  }

  /**
   * Adds a message to the message queue which should be send to a peer identified by a public key.
   * It is not checked if the message was successfully received or if the peer exists.
   *
   * @return Returns if the message was enqueued successfully, this fails if we are
   *         not connected to the network. A friend request (FriendAddPayload) is an exception,
   *         it is saved and sent when a connection is established.
   */
  private def enqueuePacket(key: PublicKey, content: Payload): Boolean = {
    state.connection.fold({
      content match {
        case _: FriendAddPayload =>
          state = state.copy(
            messages = state.messages.enqueue(NetworkMessage(state.self.publicKey, key, content))
          )
          true
        case _ => false
      }
    })(_ => {
      state = state.copy(messages = state.messages.enqueue(NetworkMessage(state.self.publicKey, key, content)))
      true
    })
  }

  /**
   * Sends a message to a friend.
   *
   * @return True if the friend exists and is connected.
   */
  private def enqueuePacket(friendNumber: Int, content: Payload): Boolean = {
    state.friends.get(friendNumber).fold(false)(friend =>
      friend.connectionStatus != ToxConnection.NONE && enqueuePacket(friend.publicKey, content))
  }

  /**
   * Sends a message to all connected friends.
   * It silently ignores errors (e.g. if we are not connected to a server).
   */
  private def enqueuePacket(content: Payload): Unit = {
    for (friendNumber <- state.friends.keys) {
      enqueuePacket(friendNumber, content)
    }
  }

  /**
   * Sends a message to all friends, no matter if they are connected or not.
   * It silently ignores errors (e.g. if we are not connected to a server).
   */
  private def enqueuePacketAll(content: Payload): Unit = {
    for (friend <- state.friends.values) {
      enqueuePacket(friend.publicKey, content)
    }
  }

  private def handlePacket(message: NetworkMessage): Boolean = {
    findFriendNumber(message.source).fold({
      message.content match {
        case FriendAddPayload(noSpam, messageContent) =>
          if (state.noSpam == noSpam) {
            state.callbacks.friendRequestCallback.friendRequest(message.source.data, 0, messageContent)
          } else {
            logger.info("Recvd a friendRequest with the wrong no spam value")
          }
          true
        case _ =>
          logger.error("Recvd message of an unknown person")
          false
      }
    })(friendNumber => {
      message.content match {
        case LosslessPayload(data) =>
          state.callbacks.friendLosslessPacketCallback.friendLosslessPacket(friendNumber, data)
        case LossyPayload(data) =>
          state.callbacks.friendLossyPacketCallback.friendLossyPacket(friendNumber, data)
        case NamePayload(name) =>
          state = state.copy(friends = state.friends.updated(friendNumber, state.friends(friendNumber).copy(name = name)))
          state.callbacks.friendNameCallback.friendName(friendNumber, name)
        case StatusMessagePayload(statusMessage) =>
          state = state.copy(friends = state.friends.updated(friendNumber, state.friends(friendNumber).copy(statusMessage = statusMessage)))
          state.callbacks.friendStatusMessageCallback.friendStatusMessage(friendNumber, statusMessage)
        case UserStatusPayload(userStatus) =>
          state = state.copy(friends = state.friends.updated(friendNumber, state.friends(friendNumber).copy(userStatus = userStatus)))
          state.callbacks.friendStatusCallback.friendStatus(friendNumber, userStatus)
        case TypingPayload(typing) =>
          state = state.copy(friends = state.friends.updated(friendNumber, state.friends(friendNumber).copy(typing = typing)))
          state.callbacks.friendTypingCallback.friendTyping(friendNumber, typing)
        case MessagePayload(id, messageType, timeDelta, message) =>
          state.callbacks.friendMessageCallback.friendMessage(friendNumber, messageType, timeDelta, message)
          // Send a read receipt message back
          enqueuePacket(friendNumber, ReadReceiptPayload(id))
        case ReadReceiptPayload(id) =>
          state.callbacks.readReceiptCallback.friendReadReceipt(friendNumber, id)

        // The ConnectionStatus is used to discover other clients on the network
        // so it's a bit more special than the other messages.
        case ConnectionStatusPayload(connectionStatus) =>
          // Ignore unneeded messages that get sent when a friend is already discovered
          // I'm not sure if it's possible to avoid this, at least it seems complicated
          // The problem is that a person joining the network sends this message to all
          // friends to see if they are connected, the connected friends answer with the
          // same message when they receive it and the original person will answer again
          // (because it uses the same mechanism which can be found in the following lines).
          // By dropping messages from people that are already known we avoid a deadlock.
          //
          // Additionally we can't make the discovery packages special because we will get
          // problems when both clients simultaneously sent them we get more traffic than we
          // want, which leads to failing tests.
          if (connectionStatus != state.friends(friendNumber).connectionStatus) {
            state = state.copy(friends = state.friends.updated(friendNumber, state.friends(friendNumber).copy(connectionStatus = connectionStatus)))
            state.callbacks.friendConnectionStatusCallback.friendConnectionStatus(friendNumber, connectionStatus)
            // Send the same package to signal that we are connected too
            // and also send the data like name and status message
            if (connectionStatus != ToxConnection.NONE) {
              enqueuePacket(friendNumber, ConnectionStatusPayload(state.self.connectionStatus))
              enqueuePacket(friendNumber, UserStatusPayload(state.self.userStatus))
              enqueuePacket(friendNumber, NamePayload(state.self.name))
              enqueuePacket(friendNumber, StatusMessagePayload(state.self.statusMessage))
              enqueuePacket(friendNumber, TypingPayload(state.self.typing))
              //TODO more info packets?
            }
          }
        case _ =>
          logger.error("Recvd message of unknown type")
          false
      }
      true
    })
  }

  /**
   * Reads from the connection as long as data is available.
   *
   * @return If everything went well. Returns false if an error occured.
   */
  @tailrec
  private def handleConnection(con: NetworkConnection): Boolean = {
    if (con.available && con.read().fold({
      logger.error("Can't read packet from the network connection")
      false
    })(message => {
      if (message.target != state.self.publicKey) {
        logger.error("Wrong target public key in received packet")
        false
      } else {
        handlePacket(message)
      }
    })) {
      handleConnection(con)
    } else {
      true
    }
  }

  private def raiseKeyWrongLengthException[T](): T =
    raiseIllegalArgumentException[T]("Key has wrong length, must be " + ToxCoreConstants.PUBLIC_KEY_SIZE + " bytes")

  private def raiseKilledException[T](): T =
    raiseToxKilledException[T]("Can't do this opertation on a killed ToxCore instance")

  private def setCallbacks(callbacks: CallbacksState): Unit = state = state.copy(callbacks = callbacks)

  def setState(newState: ToxCoreState): Unit = state = newState

  def getState: ToxCoreState = state

  override def close(): Unit = state.connection.foreach(con => {
    state = state.copy(connection = None, closed = true, self = state.self.copy(connectionStatus = ToxConnection.NONE))
    enqueuePacket(ConnectionStatusPayload(state.self.connectionStatus))
    con.close()
    state.callbacks.selfConnectionStatusCallback.selfConnectionStatus(state.self.connectionStatus)
  })

  override def getSaveData: Array[Byte] = {
    if (state.closed) {
      raiseKilledException[Array[Byte]]()
    } else {
      ???
    }
  }

  @throws[ToxBootstrapException]
  override def bootstrap(address: String, port: Int, publicKey: Array[Byte]): Unit = {
    if (state.closed) {
      raiseKilledException[Array[Byte]]()
    } else {
      PublicKey(publicKey).fold(
        raiseKeyWrongLengthException[Unit]()
      )(serverKey => {
          try {
            // Close current connection
            close()
            val con = new NetworkConnection(address, port)
            con.write(NetworkMessage(state.self.publicKey, serverKey, BootstrapPayload))
            if (!con.read().contains(NetworkMessage(serverKey, state.self.publicKey, BootstrapPayload))) {
              raise[Unit](ToxBootstrapException.Code.BAD_HOST)
            }

            // Bootstrapping successful
            state = state.copy(connection = Some(con), serverKey = Some(serverKey), self = state.self.copy(connectionStatus = ToxConnection.TCP))
            state.callbacks.selfConnectionStatusCallback.selfConnectionStatus(state.self.connectionStatus)
            // Try to connect to friends
            enqueuePacketAll(ConnectionStatusPayload(state.self.connectionStatus))
          } catch {
            // Can't establish a connection to the server -> assume that it's the servers fault
            case _: IOException => raise[Unit](ToxBootstrapException.Code.BAD_HOST)
          }
        })
    }
  }

  @throws[ToxNewException]
  override def load(options: ToxOptions): ToxCoreTcpMock = {
    if (state.closed) {
      raiseKilledException[ToxCoreTcpMock]()
    } else {
      ???
    }
  }

  @throws[ToxBootstrapException]
  override def addTcpRelay(address: String, port: Int, publicKey: Array[Byte]): Unit = {
    if (state.closed) {
      raiseKilledException[Unit]()
    } else {
      raiseUnsupportedOperationException[Unit]("ToxCoreTcpMock only supports connecting to one server at a time")
    }
  }

  override def callbackSelfConnectionStatus(callback: SelfConnectionStatusCallback): Unit =
    setCallbacks(state.callbacks.copy(selfConnectionStatusCallback = callback))

  @throws[ToxGetPortException]
  override def getUdpPort: Int = {
    if (state.closed) {
      raiseKilledException[Int]()
    } else {
      raise[Int](ToxGetPortException.Code.NOT_BOUND)
    }
  }

  @throws[ToxGetPortException]
  override def getTcpPort: Int = {
    if (state.closed) {
      raiseKilledException[Int]()
    } else {
      state.connection.fold(
        raise[Int](ToxGetPortException.Code.NOT_BOUND)
      )(_.getPort)
    }
  }

  override def getDhtId: Array[Byte] = {
    if (state.closed) {
      raiseKilledException[Array[Byte]]()
    } else {
      raiseUnsupportedOperationException[Array[Byte]]("ToxCoreTcpMock only supports connecting to a server")
    }
  }

  override def iterationInterval: Int = {
    if (state.closed) {
      raiseKilledException[Int]()
    } else {
      Math.max((lastUpdateTime + UPDATE_INTERVAL - System.currentTimeMillis).toInt, 0)
    }
  }

  override def iterate(): Unit = {
    if (state.closed) {
      raiseKilledException[Unit]()
    } else {
      lastUpdateTime = System.currentTimeMillis

      state.connection.fold({
        // Try to connect to a local server ("lan detection")
        try {
          bootstrap("localhost", ToxCoreConstants.DEFAULT_START_PORT, PublicKey.empty.data)
        } catch {
          case _: Exception => // Do nothing on failure
        }
      })(con => {
        sendMessages()
        // Close the connection when an error occured
        if (!handleConnection(con)) {
          close()
        }
      })
    }
  }

  override def getPublicKey: Array[Byte] = {
    if (state.closed) {
      raiseKilledException[Array[Byte]]()
    } else {
      state.self.publicKey.data
    }
  }

  override def getSecretKey: Array[Byte] = {
    if (state.closed) {
      raiseKilledException[Array[Byte]]()
    } else {
      ???
    }
  }

  override def setNoSpam(noSpam: Int): Unit = {
    if (state.closed) {
      raiseKilledException[Unit]()
    } else {
      state = state.copy(noSpam = NoSpam(noSpam))
    }
  }

  override def getNoSpam: Int = {
    if (state.closed) {
      raiseKilledException[Int]()
    } else {
      state.noSpam.data
    }
  }

  override def getAddress: Array[Byte] = {
    if (state.closed) {
      raiseKilledException[Array[Byte]]()
    } else {
      val addr = getPublicKey ++ ByteBuffer.allocate(Integer.SIZE).putInt(getNoSpam).array
      addr ++ createChecksum(addr)
    }
  }

  @throws[ToxSetInfoException]
  override def setName(newName: Array[Byte]): Unit = {
    if (state.closed) {
      raiseKilledException[Unit]()
    } else {
      if (newName.length > ToxCoreConstants.MAX_NAME_LENGTH) {
        raise[Unit](ToxSetInfoException.Code.TOO_LONG)
      } else {
        state = state.copy(self = state.self.copy(name = newName))
        enqueuePacket(NamePayload(state.self.name))
      }
    }
  }

  override def getName: Array[Byte] = {
    if (state.closed) {
      raiseKilledException[Array[Byte]]()
    } else {
      state.self.name
    }
  }

  @throws[ToxSetInfoException]
  override def setStatusMessage(newStatusMessage: Array[Byte]): Unit = {
    if (state.closed) {
      raiseKilledException[Unit]()
    } else {
      if (newStatusMessage.length > ToxCoreConstants.MAX_STATUS_MESSAGE_LENGTH) {
        raise[Unit](ToxSetInfoException.Code.TOO_LONG)
      } else {
        state = state.copy(self = state.self.copy(statusMessage = newStatusMessage))
        enqueuePacket(StatusMessagePayload(state.self.statusMessage))
      }
    }
  }

  override def getStatusMessage: Array[Byte] = {
    if (state.closed) {
      raiseKilledException[Array[Byte]]()
    } else {
      state.self.statusMessage
    }
  }

  override def setStatus(newUserStatus: ToxUserStatus): Unit = {
    if (state.closed) {
      raiseKilledException[Unit]()
    } else {
      state = state.copy(self = state.self.copy(userStatus = newUserStatus))
      enqueuePacket(UserStatusPayload(getStatus))
    }
  }

  override def getStatus: ToxUserStatus = {
    if (state.closed) {
      raiseKilledException[ToxUserStatus]()
    } else {
      state.self.userStatus
    }
  }

  @throws[ToxFriendAddException]
  override def addFriend(address: Array[Byte], message: Array[Byte]): Int = {
    if (state.closed) {
      raiseKilledException[Int]()
    } else {
      if (address.length != ToxCoreConstants.PUBLIC_KEY_SIZE + NoSpam.SIZE + CHECKSUM_SIZE) {
        raiseKeyWrongLengthException[Int]()
      } else if (address.takeRight(CHECKSUM_SIZE).deep != createChecksum(address.dropRight(CHECKSUM_SIZE)).deep) {
        // Wrong checksum
        raise[Int](ToxFriendAddException.Code.BAD_CHECKSUM)
      } else if (state.friends.size == Integer.MAX_VALUE) {
        raise[Int](ToxFriendAddException.Code.MALLOC)
      } else {
        val noSpam = NoSpam(ByteBuffer.wrap(address.slice(
          ToxCoreConstants.PUBLIC_KEY_SIZE, ToxCoreConstants.PUBLIC_KEY_SIZE + NoSpam.SIZE
        )).getInt())
        PublicKey(address.take(ToxCoreConstants.PUBLIC_KEY_SIZE)).fold(
          raiseKeyWrongLengthException[Int]()
        )(key => {
            if (message.length < 1) {
              raise[Int](ToxFriendAddException.Code.NO_MESSAGE)
            } else if (message.length > ToxCoreConstants.MAX_FRIEND_REQUEST_LENGTH) {
              raise[Int](ToxFriendAddException.Code.TOO_LONG)
            } else if (key == state.self.publicKey) {
              raise[Int](ToxFriendAddException.Code.OWN_KEY)
            } else {
              findFriendNumber(key).fold({
                // Send friend request
                enqueuePacket(key, FriendAddPayload(noSpam, message))
                val friendNumber = getFreeFriendNumber
                state = state.copy(friends = state.friends + (friendNumber ->
                  FriendState(key, ToxConnection.NONE, ToxUserStatus.NONE, Array[Byte](), Array[Byte](), false, 0)))
                friendNumber
              })(friend => {
                // That friend exists already
                raise[Int](ToxFriendAddException.Code.ALREADY_SENT)
              })
            }
          })
      }
    }
  }

  @throws[ToxFriendAddException]
  override def addFriendNoRequest(publicKey: Array[Byte]): Int = {
    if (state.closed) {
      raiseKilledException[Int]()
    } else {
      PublicKey(publicKey).fold(
        raiseKeyWrongLengthException[Int]()
      )(key => {
          val friendNumber = getFreeFriendNumber
          state = state.copy(friends = state.friends + (friendNumber ->
            FriendState(key, ToxConnection.NONE, ToxUserStatus.NONE, Array[Byte](), Array[Byte](), false, 0)))
          // Try to connect to that friend
          enqueuePacket(key, ConnectionStatusPayload(state.self.connectionStatus))
          friendNumber
        })
    }
  }

  @throws[ToxFriendDeleteException]
  override def deleteFriend(friendNumber: Int): Unit = {
    if (state.closed) {
      raiseKilledException[Unit]()
    } else {
      state.friends.get(friendNumber).fold(
        raise[Unit](ToxFriendDeleteException.Code.FRIEND_NOT_FOUND)
      )(_ =>
          state = state.copy(friends = state.friends - friendNumber))
    }
  }

  @throws[ToxFriendByPublicKeyException]
  override def getFriendByPublicKey(publicKey: Array[Byte]): Int = {
    if (state.closed) {
      raiseKilledException[Int]()
    } else {
      PublicKey(publicKey).fold(
        raiseKeyWrongLengthException[Int]()
      )(key => {
          findFriendNumber(key).fold(
            raise[Int](ToxFriendByPublicKeyException.Code.NOT_FOUND)
          )(friendNumber => friendNumber)
        })
    }
  }

  @throws[ToxFriendGetPublicKeyException]
  override def getFriendPublicKey(friendNumber: Int): Array[Byte] = {
    if (state.closed) {
      raiseKilledException[Array[Byte]]()
    } else {
      state.friends.get(friendNumber).fold(
        raise[Array[Byte]](ToxFriendGetPublicKeyException.Code.FRIEND_NOT_FOUND)
      )(friend => friend.publicKey.data)
    }
  }

  override def friendExists(friendNumber: Int): Boolean = {
    if (state.closed) {
      raiseKilledException[Boolean]()
    } else {
      state.friends.contains(friendNumber)
    }
  }

  override def getFriendList: Array[Int] = {
    if (state.closed) {
      raiseKilledException[Array[Int]]()
    } else {
      state.friends.keys.toArray
    }
  }

  override def callbackFriendName(callback: FriendNameCallback): Unit =
    setCallbacks(state.callbacks.copy(friendNameCallback = callback))

  override def callbackFriendStatusMessage(callback: FriendStatusMessageCallback): Unit =
    setCallbacks(state.callbacks.copy(friendStatusMessageCallback = callback))

  override def callbackFriendStatus(callback: FriendStatusCallback): Unit =
    setCallbacks(state.callbacks.copy(friendStatusCallback = callback))

  override def callbackFriendConnectionStatus(callback: FriendConnectionStatusCallback): Unit =
    setCallbacks(state.callbacks.copy(friendConnectionStatusCallback = callback))

  override def callbackFriendTyping(callback: FriendTypingCallback): Unit =
    setCallbacks(state.callbacks.copy(friendTypingCallback = callback))

  @throws[ToxFriendSendMessageException]
  override def setTyping(friendNumber: Int, typing: Boolean): Unit = {
    if (state.closed) {
      raiseKilledException[Unit]()
    } else {
      state.friends.get(friendNumber).fold(
        raise[Unit](ToxFriendSendMessageException.Code.FRIEND_NOT_FOUND)
      )(friend => {
          if (friend.connectionStatus == ToxConnection.NONE ||
            !enqueuePacket(friend.publicKey, TypingPayload(typing))) {
            raise[Unit](ToxFriendSendMessageException.Code.FRIEND_NOT_CONNECTED)
          } else {
            state = state.copy(friends = state.friends.updated(
              friendNumber,
              state.friends(friendNumber).copy(typing = typing)
            ))
          }
        })
    }
  }

  @throws[ToxFriendSendMessageException]
  override def sendMessage(friendNumber: Int, messageType: ToxMessageType, timeDelta: Int, message: Array[Byte]): Int = {
    if (state.closed) {
      raiseKilledException[Int]()
    } else {
      if (message.length > ToxCoreConstants.MAX_MESSAGE_LENGTH) {
        raise[Int](ToxFriendSendMessageException.Code.TOO_LONG)
      } else if (message.length == 0) {
        raise[Int](ToxFriendSendMessageException.Code.EMPTY)
      } else {
        state.friends.get(friendNumber).fold(
          raise[Int](ToxFriendSendMessageException.Code.FRIEND_NOT_FOUND)
        )(friend => {
            if (friend.connectionStatus == ToxConnection.NONE ||
              !enqueuePacket(friend.publicKey, MessagePayload(friend.messageId, messageType, timeDelta, message))) {
              raise[Int](ToxFriendSendMessageException.Code.FRIEND_NOT_CONNECTED)
            } else {
              // Save message id
              val newMessageId = {
                if (friend.messageId == Integer.MAX_VALUE) {
                  Integer.MIN_VALUE
                } else {
                  friend.messageId + 1
                }
              }
              state = state.copy(friends = state.friends.updated(
                friendNumber,
                state.friends(friendNumber).copy(messageId = newMessageId)
              ))
              friend.messageId
            }
          })
      }
    }
  }

  override def callbackFriendReadReceipt(callback: FriendReadReceiptCallback): Unit =
    setCallbacks(state.callbacks.copy(readReceiptCallback = callback))

  override def callbackFriendRequest(callback: FriendRequestCallback): Unit =
    setCallbacks(state.callbacks.copy(friendRequestCallback = callback))

  override def callbackFriendMessage(callback: FriendMessageCallback): Unit =
    setCallbacks(state.callbacks.copy(friendMessageCallback = callback))

  @throws[ToxFileControlException]
  override def fileControl(friendNumber: Int, fileNumber: Int, control: ToxFileControl): Unit = {
    if (state.closed) {
      raiseKilledException[Unit]()
    } else {
      ???
    }
  }

  override def callbackFileRecvControl(callback: FileRecvControlCallback): Unit =
    setCallbacks(state.callbacks.copy(fileControlCallback = callback))

  @throws[ToxFileSeekException]
  override def fileSeek(friendNumber: Int, fileNumber: Int, position: Long): Unit = {
    if (state.closed) {
      raiseKilledException[Unit]()
    } else {
      ???
    }
  }

  @throws[ToxFileSendException]
  override def fileSend(friendNumber: Int, kind: Int, fileSize: Long, fileId: Array[Byte], filename: Array[Byte]): Int = {
    if (state.closed) {
      raiseKilledException[Int]()
    } else {
      ???
    }
  }

  @throws[ToxFileSendChunkException]
  override def fileSendChunk(friendNumber: Int, fileNumber: Int, position: Long, data: Array[Byte]): Unit = {
    if (state.closed) {
      raiseKilledException[Unit]()
    } else {
      ???
    }
  }

  @throws[ToxFileGetException]
  override def fileGetFileId(friendNumber: Int, fileNumber: Int): Array[Byte] = {
    if (state.closed) {
      raiseKilledException[Array[Byte]]()
    } else {
      ???
    }
  }

  override def callbackFileChunkRequest(callback: FileChunkRequestCallback): Unit =
    setCallbacks(state.callbacks.copy(fileRequestChunkCallback = callback))

  override def callbackFileRecv(callback: FileRecvCallback): Unit =
    setCallbacks(state.callbacks.copy(fileRecvCallback = callback))

  override def callbackFileRecvChunk(callback: FileRecvChunkCallback): Unit =
    setCallbacks(state.callbacks.copy(fileRecvChunkCallback = callback))

  @throws[ToxFriendCustomPacketException]
  override def sendLossyPacket(friendNumber: Int, data: Array[Byte]): Unit = {
    if (state.closed) {
      raiseKilledException[Unit]()
    } else {
      state.friends.get(friendNumber).fold(
        raise[Unit](ToxFriendCustomPacketException.Code.FRIEND_NOT_FOUND)
      )(friend => {
          if (!enqueuePacket(friend.publicKey, LossyPayload(data))) {
            raise[Unit](ToxFriendCustomPacketException.Code.FRIEND_NOT_CONNECTED)
          }
        })
    }
  }

  override def callbackFriendLossyPacket(callback: FriendLossyPacketCallback): Unit =
    setCallbacks(state.callbacks.copy(friendLossyPacketCallback = callback))

  @throws[ToxFriendCustomPacketException]
  override def sendLosslessPacket(friendNumber: Int, data: Array[Byte]): Unit = {
    if (state.closed) {
      raiseKilledException[Unit]()
    } else {
      state.friends.get(friendNumber).fold(
        raise[Unit](ToxFriendCustomPacketException.Code.FRIEND_NOT_FOUND)
      )(friend => {
          if (friend.connectionStatus == ToxConnection.NONE || !enqueuePacket(friend.publicKey, LosslessPayload(data))) {
            raise[Unit](ToxFriendCustomPacketException.Code.FRIEND_NOT_CONNECTED)
          }
        })
    }
  }

  override def callbackFriendLosslessPacket(callback: FriendLosslessPacketCallback): Unit =
    setCallbacks(state.callbacks.copy(friendLosslessPacketCallback = callback))
}
