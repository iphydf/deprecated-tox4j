package com.nacl.crypto_onetimeauth

import com.nacl.ByteArray

object Poly1305 {

  def apply(out: ByteArray, in: ByteArray, k: ByteArray): Int = ???

  def verify(h: ByteArray, in: ByteArray, k: ByteArray): Int = ???

}
