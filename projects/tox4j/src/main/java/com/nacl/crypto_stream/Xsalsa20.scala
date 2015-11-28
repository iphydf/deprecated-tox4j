package com.nacl.crypto_stream

import com.nacl._
import com.nacl.crypto_core.Hsalsa20

object Xsalsa20 {

  val KeyBytes = 32
  val NonceBytes = 24

  def apply(c: ByteArray, n: ByteArray, k: ByteArray): Int = {
    val subKey = ByteArray.ofDim(32)
    Hsalsa20(subKey, n, k, Sodium.sigma)
    Salsa20(c, n + 16, subKey)
  }

  def xor(c: ByteArray, m: ByteArray, n: ByteArray, k: ByteArray): Int = ???

  def xor_ic(c: ByteArray, m: ByteArray, n: ByteArray, ic: Long, k: ByteArray): Int = ???

}
