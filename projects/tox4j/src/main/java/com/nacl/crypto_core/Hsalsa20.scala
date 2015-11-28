package com.nacl.crypto_core

import com.nacl.ByteArray

object Hsalsa20 {

  val OutputBytes = 32
  val InputBytes = 16
  val KeyBytes = 32
  val ConstBytes = 16

  def apply(out: ByteArray, in: ByteArray, k: ByteArray, c: ByteArray): Int = ???

}
