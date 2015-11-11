package im.tox.tox4j

import im.tox.tox4j.core.{PublicKey, Port}

final case class DhtNode(ipv4: String, ipv6: String, udpPort: Port, tcpPort: Port, dhtId: PublicKey) {

  def this(ipv4: String, ipv6: String, udpPort: Int, tcpPort: Int, dhtId: String) {
    this(
      ipv4, ipv6,
      Port.unsafeFromInt(udpPort),
      Port.unsafeFromInt(tcpPort),
      PublicKey.unsafeFromByteArray(ToxCoreTestBase.parsePublicKey(dhtId))
    )
  }

  def this(ipv4: String, ipv6: String, port: Int, dhtId: String) {
    this(ipv4, ipv6, port, port, dhtId)
  }

}
