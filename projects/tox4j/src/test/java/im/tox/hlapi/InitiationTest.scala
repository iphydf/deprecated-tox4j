package im.tox.hlapi

import im.tox.hlapi.state.ConnectionState.ConnectionStatus

final class InitiationTest extends BrownConyTestBase {
  override def newChatClient(name: String, friendName: String) = new ChatClient {
    override def receiveFriendConnectionStatus(friendNumber: Int, connectionStatus: ConnectionStatus): Unit = {

    }
  }
}
