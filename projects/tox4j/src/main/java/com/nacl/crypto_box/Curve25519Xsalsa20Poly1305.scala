package com.nacl.crypto_box

import com.nacl.crypto_core.Hsalsa20
import com.nacl.crypto_hash.Sha512
import com.nacl.crypto_scalarmult.Curve25519
import com.nacl.crypto_secretbox.Xsalsa20Poly1305
import com.nacl.{ByteArray, CryptoBox, Sodium}

object Curve25519Xsalsa20Poly1305 extends CryptoBox {

  private val n = ByteArray.ofDim(16)

  val Primitive = "curve25519xsalsa20poly1305"
  val SeedBytes = 32
  val PublicKeyBytes = 32
  val SecretKeyBytes = 32
  val BeforeNmBytes = 32
  val NonceBytes = 24
  val ZeroBytes = 32
  val BoxZeroBytes = 16
  val MacBytes = ZeroBytes - BoxZeroBytes

  def seedKeyPair(pk: ByteArray, sk: ByteArray, seed: ByteArray): Unit = {
    val hash = ByteArray.ofDim(Sha512.Bytes)
    System.arraycopy(hash.array, 0, sk, 0, 32)
    Curve25519.base(pk, sk)
  }

  def keyPair(pk: ByteArray, sk: ByteArray): Unit = {
    Sodium.randombytes(sk)
    Curve25519.base(pk, sk)
  }

  def beforeNm(k: ByteArray, pk: ByteArray, sk: ByteArray): Int = {
    val s = ByteArray.ofDim(32)
    if (Curve25519.apply(s, sk, pk) != 0) {
      -1
    } else {
      Hsalsa20(k, n, s, Sodium.sigma)
    }
  }

  def apply(c: ByteArray, m: ByteArray, n: ByteArray, k: ByteArray): Int = {
    Xsalsa20Poly1305.apply(c, m, n, k)
  }

  def unapply(m: ByteArray, c: ByteArray, n: ByteArray, k: ByteArray): Int = {
    Xsalsa20Poly1305.unapply(m, c, n, k)
  }

}
