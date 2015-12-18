package im.tox.hlapi

import com.typesafe.scalalogging.Logger
import im.tox.hlapi.Client.ToxClientConnection
import im.tox.hlapi.Event._
import org.slf4j.LoggerFactory

import scalaz.stream.Process

/**
 * TODO(iphydf): Write comments.
 */
@SuppressWarnings(Array("org.brianmckenna.wartremover.warts.Any"))
object EventDispatch {

  private val logger = Logger(LoggerFactory.getLogger(getClass))

  private def dispatch1[S](event: Event, handler: EventProcessor[S], state: S): Process[ToxClientConnection, S] = {
    event match {
      case SelfConnectionStatus(connectionStatus) =>
        handler.selfConnectionStatus(connectionStatus)(state)
      case FriendTyping(friendNumber, isTyping) =>
        handler.friendTyping(friendNumber, isTyping)(state)
      case FriendStatusMessage(friendNumber, message) =>
        handler.friendStatusMessage(friendNumber, message)(state)
      case FileChunkRequest(friendNumber, fileNumber, position, length) =>
        handler.fileChunkRequest(friendNumber, fileNumber, position, length)(state)
      case FileRecvChunk(friendNumber, fileNumber, position, data) =>
        handler.fileRecvChunk(friendNumber, fileNumber, position, data)(state)
      case FriendConnectionStatus(friendNumber, connectionStatus) =>
        handler.friendConnectionStatus(friendNumber, connectionStatus)(state)
      case FriendRequest(publicKey, timeDelta, message) =>
        handler.friendRequest(publicKey, timeDelta, message)(state)
      case FriendLossyPacket(friendNumber, data) =>
        handler.friendLossyPacket(friendNumber, data)(state)
      case FriendStatus(friendNumber, status) =>
        handler.friendStatus(friendNumber, status)(state)
      case FriendReadReceipt(friendNumber, messageId) =>
        handler.friendReadReceipt(friendNumber, messageId)(state)
      case FriendName(friendNumber, name) =>
        handler.friendName(friendNumber, name)(state)
      case FriendLosslessPacket(friendNumber, data) =>
        handler.friendLosslessPacket(friendNumber, data)(state)
      case FriendMessage(friendNumber, messageType, timeDelta, message) =>
        handler.friendMessage(friendNumber, messageType, timeDelta, message)(state)
      case FileRecv(friendNumber, fileNumber, kind, fileSize, filename) =>
        handler.fileRecv(friendNumber, fileNumber, kind, fileSize, filename)(state)
      case FileRecvControl(friendNumber, fileNumber, control) =>
        handler.fileRecvControl(friendNumber, fileNumber, control)(state)
      case AudioReceiveFrame(friendNumber, pcm, channels, samplingRate) =>
        handler.audioReceiveFrame(friendNumber, pcm, channels, samplingRate)(state)
      case BitRateStatus(friendNumber, audioBitRate, videoBitRate) =>
        handler.bitRateStatus(friendNumber, audioBitRate, videoBitRate)(state)
      case Call(friendNumber, audioEnabled, videoEnabled) =>
        handler.call(friendNumber, audioEnabled, videoEnabled)(state)
      case CallState(friendNumber, callState) =>
        handler.callState(friendNumber, callState)(state)
      case VideoReceiveFrame(friendNumber, width, height, y, u, v, yStride, uStride, vStride) =>
        handler.videoReceiveFrame(friendNumber, width, height, y, u, v, yStride, uStride, vStride)(state)
    }
  }

  def dispatch[S](event: Event, handlers: EventProcessor[S]*)(state: S): Process[ToxClientConnection, S] = {
    handlers match {
      case Nil =>
        logger.debug(s"Finished processing $event")
        Process.emit(state)
      case handler +: rest =>
        logger.debug(s"Dispatching event to $handler")
        dispatch1(event, handler, state).flatMap(dispatch(event, rest: _*))
    }

  }

}
