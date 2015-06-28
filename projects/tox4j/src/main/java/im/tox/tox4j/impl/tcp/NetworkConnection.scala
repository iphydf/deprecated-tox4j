package im.tox.tox4j.impl.tcp

import java.io.{ IOException, BufferedInputStream, BufferedOutputStream, ObjectInputStream, ObjectOutputStream }
import java.net.Socket

import im.tox.tox4j.impl.tcp.messages.NetworkMessage

class NetworkConnection(private val socket: Socket) {
  private val output: ObjectOutputStream = new ObjectOutputStream(socket.getOutputStream)
  // Buffer the input to see if something is available
  private val inputBuffer: BufferedInputStream = new BufferedInputStream(socket.getInputStream)
  private val input: ObjectInputStream = new ObjectInputStream(inputBuffer)

  def this(address: String, port: Int) = this(new Socket(address, port))

  def getPort: Int = socket.getPort

  def write(message: NetworkMessage): Unit = output.synchronized {
    output.writeObject(message)
  }

  def read(): Option[NetworkMessage] = try {
    input.synchronized {
      input.readObject match {
        case msg: NetworkMessage => Some(msg)
        case _                   => None
      }
    }
  } catch {
    case _: IOException => None
  }

  def available: Boolean = input.synchronized {
    inputBuffer.available > 0
  }

  def close(): Unit = output.synchronized {
    // Don't synchronize to input, that is locked most of the time so the socket
    // won't close.
    socket.close()
  }
}
