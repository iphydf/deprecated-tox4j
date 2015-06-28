package im.tox.tox4j.impl.tcp.server

import java.io.{ IOException, EOFException, InputStream, OutputStream }
import java.net.{ ServerSocket, Socket, SocketException, SocketTimeoutException }
import scala.annotation.tailrec
import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory

import im.tox.tox4j.core.ToxCoreConstants
import im.tox.tox4j.impl.tcp._
import im.tox.tox4j.impl.tcp.messages._

class Server(val publicKey: PublicKey) {
  private val SOCKET_TIMEOUT = 1000

  private val logger = Logger(LoggerFactory.getLogger(this.getClass))
  private val server = new ServerSocket(ToxCoreConstants.DEFAULT_START_PORT)

  // scalastyle:ignore
  private var connections: Map[PublicKey, NetworkConnection] = Map[PublicKey, NetworkConnection]()

  private val thread: Thread = new Thread(new Runnable {
    def run(): Unit = {
      server.setSoTimeout(SOCKET_TIMEOUT)
      while (!Thread.interrupted) {
        try {
          val client = server.accept()
          new Thread(new Runnable {
            def run(): Unit = {
              handleBootstrap(new NetworkConnection(client))
            }
          }).start()
        } catch {
          // When the accept() time is over
          case e: SocketTimeoutException =>
        }
      }
    }
  })

  /**
   * Handles the startup and end of a connection.
   */
  private def handleBootstrap(connection: NetworkConnection): Unit = {
    try {
      connection.read().fold({
        logger.error("Failed establishing connection to client")
      })(message => {
        if (message.target != publicKey) {
          logger.error("Client sent wrong server address while bootstrapping")
        } else {
          synchronized {
            connections = connections + (message.source -> connection)
          }
          // We have to remove the connection from the list afterwards
          try {
            // Resend bootstrap package to confirm the connection
            connection.write(NetworkMessage(publicKey, message.source, BootstrapPayload))

            handleConnection(connection, message.source)
          } finally {
            synchronized {
              connections -= message.source
            }
          }
        }
      })
    } catch {
      case ex: IOException =>
        logger.error("Connection error: " + ex.toString)
    } finally {
      connection.close()
    }
  }

  /**
   * Waits and receives packages from a connection.
   */
  @tailrec
  private def handleConnection(connection: NetworkConnection, clientKey: PublicKey): Unit = {
    // Wait for packages
    if (connection.read().fold(false)(message => {
      if (message.source != clientKey) {
        logger.error("Public key of message doesn't match the connection")
        false
      } else {
        handlePacket(message)
      }
    })) {
      handleConnection(connection, clientKey)
    }
  }

  /**
   * Handles an incomming packet from a connection.
   * First, the server tries to find a client with a matching public key.
   * If that isn't successful, he tests if the package was meant for himself.
   *
   * @return If the packet was handled successfully.
   */
  private def handlePacket(message: NetworkMessage): Boolean = connections.get(message.target).fold({
    if (message.target == publicKey) {
      logger.error("Server can't handle messages at the moment")
      false
    } else {
      // Ignore some packages that can't be received. That happens when the client tries
      // to connect to friends but they are not connected to the network.
      message.content match {
        case _: ConnectionStatusPayload => true
        case _ =>
          logger.error("Received package for unknown connection (" + message.target + ")")
          false
      }
    }
  })(con => {
    con.write(message)
    true
  })

  def start(): Unit = thread.start()

  def stop(): Unit = {
    // Close server
    thread.interrupt()
    // Close connections
    // Copy the map to keep it
    val cons = connections.values
    for (con <- cons) {
      con.close()
    }
    // Clear connection map
    synchronized {
      connections = connections.empty
    }
  }

  def getPort: Int = server.getLocalPort
}
