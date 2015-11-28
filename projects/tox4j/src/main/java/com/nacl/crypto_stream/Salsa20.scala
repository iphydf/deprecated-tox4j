package com.nacl.crypto_stream

import com.nacl.ByteArray

object Salsa20 {

  val KeyBytes = 32
  val NonceBytes = 8

  def apply(c: ByteArray, n: ByteArray, k: ByteArray): Int = ???

  def xor(c: ByteArray, m: ByteArray, n: ByteArray, k: ByteArray): Int = ???

  def xor_ic(c: ByteArray, m: ByteArray, n: ByteArray, ic: Long, k: ByteArray): Int = ???

}
