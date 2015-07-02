package im.tox.gui.events

import im.tox.client.hlapi.entity.{CoreState, Event}
import Event.SendFriendMessageEvent
import im.tox.client.hlapi.adapter.ToxAdapter
import im.tox.gui.MainView
import im.tox.gui.MainView._
import ToxAdapter._
import CoreState._

import javax.swing._
import java.awt.event.ActionEvent
import java.awt.event.ActionListener

final class SendButtonOnAction(toxGui: MainView) extends ActionListener {

  override def actionPerformed(event: ActionEvent): Unit = {
    val friendNumber = toxGui.friendList.getSelectedIndex
    if (friendNumber == -1) {
      JOptionPane.showMessageDialog(toxGui, "Select a friend to send a message to")
    }
    val message = {
      if (toxGui.messageRadioButton.isSelected) {
        Message(NormalMessage(), 0, toxGui.messageText.getText.getBytes)
        toxGui.addMessage("Sent message to ", friendNumber + ": " + toxGui.messageText.getText)
      } else if (toxGui.actionRadioButton.isSelected) {
        Message(ActionMessage(), 0, toxGui.messageText.getText.getBytes)
       toxGui.addMessage("Sent action to ", friendNumber + ": " + toxGui.messageText.getText)
      }
    }

    toxGui.messageText.setText("")
  }

}
