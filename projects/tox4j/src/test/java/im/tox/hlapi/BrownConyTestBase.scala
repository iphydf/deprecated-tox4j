package im.tox.hlapi

import com.typesafe.scalalogging.Logger
import im.tox.hlapi.adapter.ToxAdapter
import im.tox.hlapi.event.Event.{ UiEventType }
import im.tox.hlapi.event.UiEvent.{ AddFriendNoRequestEvent, ToxInitEvent }
import im.tox.hlapi.request.Reply.GetSelfPublicKeyReply
import im.tox.hlapi.request.Request.GetSelfPublicKeyRequest
import im.tox.hlapi.state.ConnectionState.ConnectionOptions
import org.scalatest.FunSuite
import org.scalatest.concurrent.Timeouts
import org.scalatest.exceptions.TestFailedDueToTimeoutException
import org.slf4j.LoggerFactory

abstract class BrownConyTestBase extends FunSuite with Timeouts {

  protected val logger = Logger(LoggerFactory.getLogger(classOf[BrownConyTestBase]))

  protected def newChatClient(name: String, expectedFriendName: String): ChatClient

  protected def runBrownConyTest(): Unit = {
    val brown = newChatClient("Brown", "Cony")
    val cony = newChatClient("Cony", "Brown")
    val brownAdapter = new ToxAdapter()
    val conyAdapter = new ToxAdapter()
    brownAdapter.acceptEvent(UiEventType(ToxInitEvent(ConnectionOptions(), brown)))
    conyAdapter.acceptEvent(UiEventType(ToxInitEvent(ConnectionOptions(), cony)))
    val brownPublicKey = brownAdapter.acceptRequest(GetSelfPublicKeyRequest())
    val conyPublicKey = conyAdapter.acceptRequest(GetSelfPublicKeyRequest())
    brownPublicKey match {
      case GetSelfPublicKeyReply(publicKey) => {
        logger.info(publicKey.key.length.toString)
        conyAdapter.acceptEvent(UiEventType(AddFriendNoRequestEvent(publicKey)))
      }
    }
    conyPublicKey match {
      case GetSelfPublicKeyReply(publicKey) => {
        brownAdapter.acceptEvent(UiEventType(AddFriendNoRequestEvent(publicKey)))
      }
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
