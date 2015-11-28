package com.nacl.crypto_secretbox

import com.nacl.crypto_onetimeauth.Poly1305
import com.nacl.crypto_stream.Xsalsa20
import com.nacl.{ByteArray, CryptoSecretBox, Sodium}

object Xsalsa20Poly1305 extends CryptoSecretBox {

  val Primitive = "xsalsa20poly1305"

  val KeyBytes = 32
  val NonceBytes = 24
  val ZeroBytes = 32
  val BoxZeroBytes = 16
  val MacBytes = ZeroBytes - BoxZeroBytes

  def apply(c: ByteArray, m: ByteArray, n: ByteArray, k: ByteArray): Int = {
    if (m.length < 32) {
      -1
    } else {
      Xsalsa20.xor(c, m, n, k)
      Poly1305(c + 16, c + 32, c)
      Sodium.memzero(c, 16)
      0
    }
  }

  def unapply(m: ByteArray, c: ByteArray, n: ByteArray, k: ByteArray): Int = {
    if (c.length < 32) {
      -1
    } else {
      val subKey = ByteArray.ofDim(32)
      Xsalsa20(subKey, n, k)
      if (Poly1305.verify(c + 16, c + 32, subKey) != 0) {
        -1
      }
      Xsalsa20.xor(m, c, n, k)
      Sodium.memzero(m, 32)
      0
    }
  }

}
