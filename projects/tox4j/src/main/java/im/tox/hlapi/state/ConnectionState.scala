package im.tox.hlapi.state

object ConnectionState {

  sealed abstract class ConnectionStatus
  final case class Connect(connectionOptions: ConnectionOptions) extends ConnectionStatus
  final case class Disconnect() extends ConnectionStatus

  final case class ConnectionOptions(
    enableIPv6: Boolean = true,
    enableUdp: Boolean = true,
    proxyOption: ProxyOption = NoProxy(),
    saveDataOption: SaveDataOption = NoSaveData()
  )

  sealed abstract class ProxyOption
  final case class Http(proxyHost: String, proxyPort: Int) extends ProxyOption
  final case class Socks5(proxyHost: String, proxyPort: Int) extends ProxyOption
  final case class NoProxy() extends ProxyOption

  sealed abstract class SaveDataOption
  final case class NoSaveData() extends SaveDataOption
  final case class ToxSave(data: Array[Byte]) extends SaveDataOption
}
