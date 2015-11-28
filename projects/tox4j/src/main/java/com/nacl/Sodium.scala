package com.nacl

import java.security.SecureRandom

object Sodium {

  private val random = new SecureRandom()

  def randombytes(sk: ByteArray): Unit = {
    random.nextBytes(sk.array)
  }

  def memzero(bytes: ByteArray, count: Int): Unit = {
    for (i <- bytes.indices.take(count)) {
      bytes(i) = 0
    }
  }

  val sigma = ByteArray("expand 32-byte k")
  assert(sigma.length == 16)

}
