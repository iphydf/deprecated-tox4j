package im.tox.hlapi.state

object PublicKeyState {
  final case class PublicKey(key: Array[Byte] = Array.ofDim[Byte](0))
}
