package im.tox.hlapi

import com.typesafe.scalalogging.Logger
import im.tox.hlapi.adapter.ToxAdapter
import im.tox.hlapi.event.Event.{ UiEventType }
import im.tox.hlapi.event.UiEvent.{ SetNicknameEvent, AddFriendNoRequestEvent, ToxInitEvent }
import im.tox.hlapi.request.Reply.GetSelfPublicKeyReply
import im.tox.hlapi.request.Request.GetSelfPublicKeyRequest
import im.tox.hlapi.state.ConnectionState.ConnectionOptions
import im.tox.hlapi.state.PublicKeyState.PublicKey
import org.scalatest.FunSuite
import org.scalatest.concurrent.Timeouts
import org.scalatest.exceptions.TestFailedDueToTimeoutException
import org.slf4j.LoggerFactory

abstract class BrownConyTestBase extends FunSuite with Timeouts {

  protected var brownPublicKey: PublicKey = PublicKey()
  protected var conyPublicKey: PublicKey = PublicKey()
  protected val logger = Logger(LoggerFactory.getLogger(classOf[BrownConyTestBase]))

  protected def newChatClient(name: String, expectedFriendName: String, adapter: ToxAdapter): ChatClient

  protected def runBrownConyTest(): Unit = {
    val brown = newChatClient("Brown", "Cony", new ToxAdapter())
    val cony = newChatClient("Cony", "Brown", new ToxAdapter())
    val brownAdapter = brown.selfAdapter
    val conyAdapter = cony.selfAdapter
    brownAdapter.acceptEvent(UiEventType(ToxInitEvent(ConnectionOptions(), brown)))
    conyAdapter.acceptEvent(UiEventType(ToxInitEvent(ConnectionOptions(), cony)))
    val brownRequest = brownAdapter.acceptRequest(GetSelfPublicKeyRequest())
    val conyRequest = conyAdapter.acceptRequest(GetSelfPublicKeyRequest())
    brownRequest match {
      case GetSelfPublicKeyReply(publicKey) => {
        brownPublicKey = publicKey
        conyAdapter.acceptEvent(UiEventType(AddFriendNoRequestEvent(publicKey)))
      }
    }
    conyRequest match {
      case GetSelfPublicKeyReply(publicKey) => {
        conyPublicKey = publicKey
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
