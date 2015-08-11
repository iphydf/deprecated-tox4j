package im.tox.hlapi.state

object PublicKeyState {
  final case class PublicKey(key: Array[Byte] = Array.ofDim[Byte](0))
  final case class Address(address: Array[Byte] = Array.ofDim[Byte](0))
}
