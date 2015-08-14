package im.tox.hlapi.multiclients

import im.tox.hlapi.adapter.ToxAdapter
import im.tox.hlapi.event.NetworkEvent
import im.tox.hlapi.request.Reply.{ GetSelfAddressReply, GetSelfPublicKeyReply }
import im.tox.hlapi.request.Request.{ GetSelfAddressRequest, GetSelfPublicKeyRequest }
import im.tox.hlapi.state.ConnectionState.ConnectionOptions
import im.tox.hlapi.state.PublicKeyState.{ Address, PublicKey }
import org.scalatest.FunSuite

import scala.collection.immutable.Queue
import scala.util.control.Breaks._

abstract class MultiClientTestBase extends FunSuite {

  protected var adapter: Array[ToxAdapter] = null
  protected var publicKey: Array[PublicKey] = null
  protected var address: Array[Address] = null
  protected var isFinished: Array[Boolean] = null

  protected def newChatClient(id: Int): ChatClient

  def runTest(num: Int): Unit = {
    adapter = Array.ofDim[ToxAdapter](num)
    publicKey = Array.ofDim[PublicKey](num)
    address = Array.ofDim[Address](num)
    isFinished = Array.ofDim[Boolean](num)
    val eventQueue = Array.ofDim[Queue[NetworkEvent]](num)
    for (x <- 0 until num) {
      val client = newChatClient(x)
      adapter(x) = new ToxAdapter(client)
      adapter(x).initToxSession(ConnectionOptions())
      val publicKeyReply = adapter(x).acceptRequest(GetSelfPublicKeyRequest())
      publicKeyReply match {
        case GetSelfPublicKeyReply(key) => {
          publicKey(x) = key
        }
      }
      val addressReply = adapter(x).acceptRequest(GetSelfAddressRequest())
      addressReply match {
        case GetSelfAddressReply(addr) => {
          address(x) = addr
        }
      }
      eventQueue(x) = Queue[NetworkEvent]()
      isFinished(x) = false
      while (!allFinished) {
        for (x <- 0 until num) {
          while (eventQueue(x).nonEmpty) {
            val remain = eventQueue(x).dequeue
            eventQueue(x) = remain._2
            adapter(x).acceptNetworkEvent(remain._1)
          }
        }
        Thread.sleep(adapter(0).getIterateInterval)
        for (x <- 0 until num) {
          if (adapter(x).isInit) {
            eventQueue(x) = adapter(x).iterate(eventQueue(x))
          }
        }
      }

      for (x <- 0 until num) {
        if (adapter(x).isInit) {
          adapter(x).closeToxSession()
        }
      }
    }

  }

  private def allFinished: Boolean = {
    var finished = true
    for (client <- isFinished) {
      if (!client) {
        finished = false
        break
      }
    }
    finished
  }
  test("5 Clients Test") {
    runTest(5)
  }
  /*
  test("10 Clients Test") {
    runTest(10)
  }

  test("30 Clients Test") {
    runTest(30)
  }

  test("70 Clients Test") {
    runTest(70)
  }*/
}
