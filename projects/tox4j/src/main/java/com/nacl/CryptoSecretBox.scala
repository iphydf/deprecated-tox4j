package com.nacl

abstract class CryptoSecretBox extends Algorithm {

  /**
   * crypto_secretbox_keybytes
   */
  def KeyBytes: Int

  /**
   * crypto_secretbox_noncebytes
   */
  def NonceBytes: Int

  /**
   * crypto_secretbox_zerobytes
   */
  def ZeroBytes: Int

  /**
   * crypto_secretbox_boxzerobytes
   */
  def BoxZeroBytes: Int

  /**
   * crypto_secretbox_macbytes
   */
  def MacBytes: Int

  /**
   * crypto_secretbox
   */
  def apply(c: ByteArray, m: ByteArray, n: ByteArray, k: ByteArray): Int

  /**
   * crypto_secretbox_open
   */
  def unapply(m: ByteArray, c: ByteArray, n: ByteArray, k: ByteArray): Int

}
