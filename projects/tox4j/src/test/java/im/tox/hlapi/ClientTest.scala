package im.tox.hlapi

import com.typesafe.scalalogging.Logger
import im.tox.client.{ToxClientOptions, ToxClientState}
import im.tox.core.network.{NetworkCoreTest, Port}
import im.tox.hlapi.Client.ToxClientConnection
import im.tox.hlapi.Event.{VideoReceiveFrame, _}
import im.tox.tox4j.core.data.{ToxFriendAddress, ToxPublicKey}
import im.tox.tox4j.core.options.{SaveDataOptions, ToxOptions}
import im.tox.tox4j.testing.GetDisjunction._
import org.scalatest.FunSuite
import org.slf4j.LoggerFactory

import scalaz.stream._

final class ClientTest extends FunSuite {

  private val logger = Logger(LoggerFactory.getLogger(getClass))

  def processEvent(event: Event, state: ToxClientState): Process[ToxClientConnection, ToxClientState] = {
    event match {
      case SelfConnectionStatus(connectionStatus) =>
      case FriendTyping(friendNumber, isTyping) =>
      case FriendStatusMessage(friendNumber, message) =>
      case FileChunkRequest(friendNumber, fileNumber, position, length) =>
      case FileRecvChunk(friendNumber, fileNumber, position, data) =>
      case FriendConnectionStatus(friendNumber, connectionStatus) =>
      case FriendRequest(publicKey, timeDelta, message) =>
      case FriendLossyPacket(friendNumber, data) =>
      case FriendStatus(friendNumber, status) =>
      case FriendReadReceipt(friendNumber, messageId) =>
      case FriendName(friendNumber, name) =>
      case FriendLosslessPacket(friendNumber, data) =>
      case FriendMessage(friendNumber, messageType, timeDelta, message) =>
      case FileRecv(friendNumber, fileNumber, kind, fileSize, filename) =>
      case FileRecvControl(friendNumber, fileNumber, control) =>
      case AudioReceiveFrame(friendNumber, pcm, channels, samplingRate) =>
      case BitRateStatus(friendNumber, audioBitRate, videoBitRate) =>
      case Call(friendNumber, audioEnabled, videoEnabled) =>
      case CallState(friendNumber, callState) =>
      case VideoReceiveFrame(friendNumber, width, height, y, u, v, yStride, uStride, vStride) =>
    }

    Process.emit(state)
  }

  test("test") {
    ToxClientOptions(Nil) { c =>
      val predefined = c.load.map(key => ToxOptions(saveData = SaveDataOptions.SecretKey(key)))
      logger.info(s"Creating ${c.count} toxes (${predefined.length} with predefined keys)")
      val defaults = List.fill(c.count - predefined.length)(ToxOptions())
      logger.info(s"Additional default toxes: ${defaults.length}")

      val clients = (predefined ++ defaults).zipWithIndex.map {
        case (options, id) =>
          Client.start(options.copy(udpEnabled = false)) {
            val node = NetworkCoreTest.nodes.headOption.get

            val bootHost = node._1.getHostName
            val bootPort = Port.unsafeFromInt(node._1.getPort)
            val bootKey = ToxPublicKey.fromHexString(node._2.toHexString).get

            // TODO(iphydf): EW!
            var state = ToxClientState(
              ToxFriendAddress.unsafeFromValue(Array.empty),
              ToxPublicKey.unsafeFromValue(Array.empty),
              Port.unsafeFromInt(0)
            )

            for {
              () <- Client.bootstrap(bootHost, bootPort, bootKey)
              profile <- ProfileManager.loadProfile(id)
              event <- Client.receive
              newState <- EventDispatch.dispatch(event, FriendListEventListener, AudioVideoEventListener)(state)
              () <- {
                if (newState.profile != state.profile) {
                  ProfileManager.saveProfile(newState.profile)
                } else {
                  Process.emit(())
                }
              }
            } yield {
              state = newState
            }
          }
      }

      clients.reduceOption(_.merge(_)).foreach(_.run.run)
    }
  }

}
