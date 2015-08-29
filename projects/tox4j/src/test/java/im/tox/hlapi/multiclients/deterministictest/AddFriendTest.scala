package im.tox.hlapi.multiclients.deterministictest

import im.tox.hlapi.multiclients.{ ChatClient, MultiClientTestBase }
import im.tox.hlapi.state.ConnectionState.ConnectionStatus

final class AddFriendTest extends MultiClientTestBase {
  override def newChatClient(id: Int) = new ChatClient(id) {
    /*override def receiveSelfConnectionStatus(connectionStatus: ConnectionStatus): Unit => {

    }*/
  }
}
