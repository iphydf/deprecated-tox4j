package com.nacl

abstract class CryptoBox {

  /**
   * crypto_box_seedbytes
   */
  def SeedBytes: Int

  /**
   * crypto_box_publickeybytes
   */
  def PublicKeyBytes: Int

  /**
   * crypto_box_secretkeybytes
   */
  def SecretKeyBytes: Int

  /**
   * crypto_box_beforenmbytes
   */
  def BeforeNmBytes: Int

  /**
   * crypto_box_noncebytes
   */
  def NonceBytes: Int

  /**
   * crypto_box_zerobytes
   */
  def ZeroBytes: Int

  /**
   * crypto_box_boxzerobytes
   */
  def BoxZeroBytes: Int

  /**
   * crypto_box_macbytes
   */
  def MacBytes: Int

  /**
   * crypto_box
   */
  final def apply(c: ByteArray, m: ByteArray, n: ByteArray, pk: ByteArray, sk: ByteArray): Int = {
    val k = ByteArray.ofDim(BeforeNmBytes)
    if (beforeNm(k, pk, sk) != 0) {
      -1
    } else {
      apply(c, m, n, k)
    }
  }

  /**
   * crypto_box_open
   */
  final def unapply(m: ByteArray, c: ByteArray, n: ByteArray, pk: ByteArray, sk: ByteArray): Int = {
    val k = ByteArray.ofDim(BeforeNmBytes)
    if (beforeNm(k, pk, sk) != 0) {
      -1
    } else {
      unapply(m, c, n, k)
    }
  }

  /**
   * crypto_box_seed_keypair
   */
  def seedKeyPair(pk: ByteArray, sk: ByteArray, seed: ByteArray): Unit

  /**
   * crypto_box_keypair
   */
  def keyPair(pk: ByteArray, sk: ByteArray): Unit

  /**
   * crypto_box_beforenm
   */
  def beforeNm(k: ByteArray, pk: ByteArray, sk: ByteArray): Int

  /**
   * crypto_box_afternm
   */
  def apply(c: ByteArray, m: ByteArray, n: ByteArray, k: ByteArray): Int

  /**
   * crypto_box_afternm_open
   */
  def unapply(m: ByteArray, c: ByteArray, n: ByteArray, k: ByteArray): Int

}
