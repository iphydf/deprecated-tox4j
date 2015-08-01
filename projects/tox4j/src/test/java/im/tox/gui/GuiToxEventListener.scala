package im.tox.gui

import javax.swing._
import im.tox.hlapi.entity.CoreState._
import im.tox.hlapi.listener.ToxClientListener

final class GuiToxEventListener(toxGui: MainView) extends ToxClientListener {

  private def addMessage(method: String, args: Any*) = {
    val str = new StringBuilder
    str.append(method)
    str.append('(')
    var first = true
    for (arg <- args) {
      if (!first) {
        str.append(", ")
      }
      str.append(arg)
      first = false
    }
    str.append(')')
    toxGui.addMessage(str.toString())
  }

  override def receiveSelfConnectionStatus(connectionStatus: ConnectionStatus) = {
    addMessage("selfConnectionStatus", connectionStatus)
  }
/*
  override def receiveFileRecvControl(friendNumber: Int, fileNumber: Int, @NotNull control: ToxFileControl)(state: ToxState): ToxState = {
    addMessage("fileRecvControl", friendNumber, fileNumber, control)

    try {
      control match {
        case ToxFileControl.RESUME =>
          toxGui.fileModel.get(friendNumber, fileNumber).resume()
        case ToxFileControl.CANCEL =>
          throw new UnsupportedOperationException("CANCEL")
        case ToxFileControl.PAUSE =>
          throw new UnsupportedOperationException("PAUSE")
      }
    } catch {
      case e: Throwable =>
        JOptionPane.showMessageDialog(toxGui, MainView.printExn(e))
    }
  }

  override def fileRecv(friendNumber: Int, fileNumber: Int, kind: Int, fileSize: Long, @NotNull filename: Array[Byte])(state: ToxState): ToxState = {
    addMessage("fileRecv", friendNumber, fileNumber, kind, fileSize, new String(filename))

    try {
      val confirmation = JOptionPane.showConfirmDialog(toxGui, "Incoming file transfer: " + new String(filename))

      val cancel =
        if (confirmation == JOptionPane.OK_OPTION) {
          val chooser = new JFileChooser
          val returnVal = chooser.showOpenDialog(toxGui)

          if (returnVal == JFileChooser.APPROVE_OPTION) {
            toxGui.fileModel.addIncoming(friendNumber, fileNumber, kind, fileSize, chooser.getSelectedFile)
            toxGui.tox.fileControl(friendNumber, fileNumber, ToxFileControl.RESUME)
            true
          } else {
            false
          }
        } else {
          false
        }

      if (cancel) {
        toxGui.tox.fileControl(friendNumber, fileNumber, ToxFileControl.CANCEL)
      }
    } catch {
      case e: Throwable =>
        JOptionPane.showMessageDialog(toxGui, MainView.printExn(e))
    }
  }

  override def fileRecvChunk(friendNumber: Int, fileNumber: Int, position: Long, @NotNull data: Array[Byte])(state: ToxState): ToxState = {
    addMessage("fileRecvChunk", friendNumber, fileNumber, position, "byte[" + data.length + ']')
    try {
      toxGui.fileModel.get(friendNumber, fileNumber).write(position, data)
    } catch {
      case e: Throwable =>
        JOptionPane.showMessageDialog(toxGui, MainView.printExn(e))
    }
  }

  override def fileChunkRequest(friendNumber: Int, fileNumber: Int, position: Long, length: Int)(state: ToxState): ToxState = {
    addMessage("fileChunkRequest", friendNumber, fileNumber, position, length)
    try {
      if (length == 0) {
        toxGui.fileModel.remove(friendNumber, fileNumber)
      } else {
        toxGui.tox.fileSendChunk(friendNumber, fileNumber, position, toxGui.fileModel.get(friendNumber, fileNumber).read(position, length))
      }
    } catch {
      case e: Throwable =>
        JOptionPane.showMessageDialog(toxGui, MainView.printExn(e))
    }
  }*/

  override def receiveFriendConnectionStatus(friendNumber: Int, connectionStatus: ConnectionStatus) = {
    addMessage("friendConnectionStatus", friendNumber, connectionStatus)
  }
/*
  override def friendLosslessPacket(friendNumber: Int, @NotNull data: Array[Byte])(state: ToxState): ToxState = {
    addMessage("friendLosslessPacket", friendNumber, readablePublicKey(data))
  }

  override def friendLossyPacket(friendNumber: Int, @NotNull data: Array[Byte])(state: ToxState): ToxState = {
    addMessage("friendLossyPacket", friendNumber, readablePublicKey(data))
  }*/

  override def receiveFriendMessage(friendNumber: Int, message: Message) = {
    addMessage("friendMessage", message)
  }

  override def receiveFriendName(friendNumber: Int, name: Array[Byte]) = {
    addMessage("friendName", friendNumber, new String(name))
  }
/*
  override def friendRequest(@NotNull publicKey: Array[Byte], timeDelta: Int, @NotNull message: Array[Byte])(state: ToxState): ToxState = {
    addMessage("friendRequest", readablePublicKey(publicKey), timeDelta, new String(message))
  }*/

  override def receiveFriendStatus(friendNumber: Int, userStatus: UserStatus) = {
    addMessage("friendStatus", friendNumber, userStatus)
  }

  override def receiveFriendStatusMessage(friendNumber: Int, message: Array[Byte]) = {
    addMessage("friendStatusMessage", friendNumber, new String(message))
  }

  override def receiveFriendTyping(friendNumber: Int, isTyping: Boolean) = {
    addMessage("friendTyping", friendNumber, isTyping)
  }

  override def receiveFriendReadReceipt(friendNumber: Int, messageId: Int) = {
    addMessage("friendReadReceipt", friendNumber, messageId)
  }
}
