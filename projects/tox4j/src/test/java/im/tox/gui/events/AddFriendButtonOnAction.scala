package im.tox.gui.events

import java.awt.event.{ ActionEvent, ActionListener }
import javax.swing._
import im.tox.client.hlapi.entity.Event
import Event.SendFriendRequestEvent
import im.tox.client.hlapi.adapter.ToxAdapter
import im.tox.gui.MainView
import im.tox.gui.MainView._
import im.tox.tox4j.ToxCoreTestBase.parsePublicKey
import ToxAdapter._

final class AddFriendButtonOnAction(toxGui: MainView) extends ActionListener {

  override def actionPerformed(event: ActionEvent): Unit = {

    val publicKey = parsePublicKey(toxGui.friendId.getText)
    val friendNumber =
      if (toxGui.friendRequest.getText.isEmpty) {
        acceptEvent(state, SendFriendRequestEvent(publicKey, None))
      } else {
        acceptEvent(state, SendFriendRequestEvent(publicKey, Some(toxGui.friendRequest.getText.getBytes)))
      }
    toxGui.addMessage("Added friend number ", friendNumber)
    toxGui.save()
    
  }

}
