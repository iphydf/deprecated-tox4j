package im.tox.gui.domain

import java.io.Serializable

import im.tox.tox4j.core.{PublicKey, ToxCoreConstants}
import im.tox.tox4j.core.enums.{ToxConnection, ToxUserStatus}

final class Friend(val publicKey: PublicKey) extends Serializable {
  require(publicKey.value.length == ToxCoreConstants.PublicKeySize)

  var name: String = "<No name>"
  @transient
  var connectionStatus: ToxConnection = null
  @transient
  var status: ToxUserStatus = null
  var statusMessage: String = null
  @transient
  var typing: Boolean = false

  override def toString: String = {
    s"Friend ($name, $connectionStatus, $status, $statusMessage)${if (typing) " [typing]" else ""}"
  }
}
