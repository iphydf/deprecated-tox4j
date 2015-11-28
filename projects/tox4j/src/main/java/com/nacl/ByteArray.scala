package com.nacl

import java.nio.charset.Charset

final class ByteArray(val array: Array[Byte], position: Int = 0) extends Seq[Byte] {

  require(position < array.length)

  override def length: Int = array.length - position

  override def apply(idx: Int): Byte = array(position + idx)
  def update(idx: Int, byte: Byte): Unit = array(position + idx) = byte

  override def iterator: Iterator[Byte] = array.iterator.drop(position)

  def +(offset: Int): ByteArray = {
    new ByteArray(array, position + offset)
  }

}

object ByteArray {

  private val ascii = Charset.forName("ASCII")

  def apply(string: String): ByteArray = {
    new ByteArray(string.getBytes(ascii))
  }

  def ofDim(size: Int): ByteArray = {
    new ByteArray(Array.ofDim(size), 0)
  }

}