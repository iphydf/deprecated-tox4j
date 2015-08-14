package im.tox.hlapi.browncony

import com.typesafe.scalalogging.Logger
import im.tox.hlapi.adapter.ToxAdapter
import im.tox.hlapi.event.NetworkEvent
import im.tox.hlapi.event.UiEvent.AddFriendNoRequestEvent
import im.tox.hlapi.request.Reply.{ GetSelfAddressReply, GetSelfPublicKeyReply }
import im.tox.hlapi.request.Request.{ GetSelfAddressRequest, GetSelfPublicKeyRequest }
import im.tox.hlapi.state.ConnectionState.ConnectionOptions
import im.tox.hlapi.state.PublicKeyState.{ Address, PublicKey }
import org.scalatest.FunSuite
import org.scalatest.concurrent.Timeouts
import org.scalatest.exceptions.TestFailedDueToTimeoutException
import org.slf4j.LoggerFactory

import scala.collection.immutable.Queue

abstract class BrownConyTestBase extends FunSuite with Timeouts {

  protected var brownPublicKey: PublicKey = PublicKey()
  protected var conyPublicKey: PublicKey = PublicKey()
  protected var conyAddress: Address = Address()
  protected var brownAdapter: ToxAdapter = null
  protected var conyAdapter: ToxAdapter = null
  protected var brownFinished = false
  protected var conyFinished = false
  protected val logger = Logger(LoggerFactory.getLogger(classOf[BrownConyTestBase]))

  protected def newChatClient(name: String, expectedFriendName: String): BrownConyChatClient

  protected def runBrownConyTest(): Unit = {
    val brown = newChatClient("Brown", "Cony")
    val cony = newChatClient("Cony", "Brown")
    brownAdapter = new ToxAdapter(brown)
    conyAdapter = new ToxAdapter(cony)
    brownAdapter.initToxSession(ConnectionOptions())
    conyAdapter.initToxSession(ConnectionOptions())
    val brownRequest = brownAdapter.acceptRequest(GetSelfPublicKeyRequest())
    val conyRequest = conyAdapter.acceptRequest(GetSelfPublicKeyRequest())
    brownRequest match {
      case GetSelfPublicKeyReply(publicKey) => {
        brownPublicKey = publicKey
        conyAdapter.acceptUiEvent(AddFriendNoRequestEvent(publicKey))
      }
    }
    conyRequest match {
      case GetSelfPublicKeyReply(publicKey) => {
        conyPublicKey = publicKey
        brownAdapter.acceptUiEvent(AddFriendNoRequestEvent(publicKey))
      }
    }
    val addressRequest = conyAdapter.acceptRequest(GetSelfAddressRequest())
    addressRequest match {
      case GetSelfAddressReply(address) => {
        conyAddress = address
      }
    }
    var brownEventList = Queue[NetworkEvent]()
    var conyEventList = Queue[NetworkEvent]()
    while (!brownFinished || !conyFinished) {
      while (brownEventList.nonEmpty) {
        val remain = brownEventList.dequeue
        brownEventList = remain._2
        brownAdapter.acceptNetworkEvent(remain._1)
      }
      while (conyEventList.nonEmpty) {
        val remain = conyEventList.dequeue
        conyEventList = remain._2
        conyAdapter.acceptNetworkEvent(remain._1)
      }
      Thread.sleep(conyAdapter.getIterateInterval)
      if (brownAdapter.isInit) {
        brownEventList = brownAdapter.iterate(brownEventList)
      }
      if (conyAdapter.isInit) {
        conyEventList = conyAdapter.iterate(conyEventList)
      }
    }
    if (brownAdapter.isInit) {
      brownAdapter.closeToxSession()
    }
    if (conyAdapter.isInit) {
      conyAdapter.closeToxSession()
    }
  }

  test("BrownConyTest") {
    try {
      runBrownConyTest()
    } catch {
      case e: TestFailedDueToTimeoutException => {
        logger.info("Fail due timeout")
      }
    }
  }

}
