package im.tox.client.hlapi

import im.tox.client.PseudoToxClient.acceptEvent
import im.tox.client.hlapi.Event._
import im.tox.client.hlapi.State._

import org.scalatest.FunSuite
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue

class StateChangeTest extends FunSuite {

  private def cleanState(): ToxState = {
    ToxState(
      UserProfile("Brown", "Hello Cony", "Cute little Brown"),
      "Online",
      "Online",
      Map[Int, Group](1 -> Group(
        GroupProfile("Line", "Town", "Line town", Map[Int, UserProfile]()),
        PublicConversation("Muted", Map[Int, Message]()),
        "line", "starred", "none"
      )),
      Map[Int, Friend](1 -> Friend(
        UserProfile("Cony", "Hello Brown", "Cute little Cony"),
        PrivateConversation("non-muted", false,
          Map[Int, Message](1 -> Message("common", 1, "blah", "unread")),
          Seq[File]()),
        "cony", "blocked", "starred", "online", "online"
      ))
    )
  }

  test("testReceiveSelfConnectionStatus") {
    val state = acceptEvent(
      cleanState(),
      ReceiveSelfConnectionStatus("Offline")
    )
    assertEquals("Offline", state.connectionStatus)
  }

  test("testReceiveFriendConnectionStatus") {
    val state = acceptEvent(
      cleanState(),
      ReceiveFriendConnectionStatus(1, "Offline")
    )
    assertEquals("Offline", state.friends(1).connectionStatus)
  }

  test("testReceiveFriendMessage") {
    val state = acceptEvent(
      cleanState(),
      ReceiveFriendMessage(1, "common", 2, "hello brown")
    )
    assertEquals("hello brown", state.friends(1).conversation.messages(1).content)
  }

  test("testReceiveFriendName") {
    val state = acceptEvent(
      cleanState(),
      ReceiveFriendName(1, "James")
    )
    assertEquals("James", state.friends(1).userProfile.nickname)
  }

  test("testReceiveFriendStatus") {
    val state = acceptEvent(
      cleanState(),
      ReceiveFriendStatus(1, "Away")
    )
    assertEquals("Away", state.friends(1).userStatus)
  }

  test("testReceiveFriendTyping") {
    val state = acceptEvent(
      cleanState(),
      ReceiveFriendTyping(1, true)
    )
    assertTrue(state.friends(1).conversation.isTyping)
  }

  test("testReceiveReadReceipt") {
    val state = acceptEvent(
      cleanState(),
      ReceiveReadReceipt(1, 1)
    )
    assertEquals("read", state.friends(1).conversation.messages(1).status)
  }

  test("testChangeConnectionStatus") {
    val state = acceptEvent(
      cleanState(),
      ChangeConnectionStatus("offline")
    )
    assertEquals("offline", state.connectionStatus)
  }

  test("testChangeUserStatus") {
    val state = acceptEvent(
      cleanState(),
      ChangeUserStatus("offline")
    )
    assertEquals("offline", state.userStatus)
  }

  test("testChangeStatusMessage") {
    val state = acceptEvent(
      cleanState(),
      ChangeStatusMessage("hi")
    )
    assertEquals("hi", state.userProfile.statusMessage)
  }

  test("testDeleteFriend") {
    val state = acceptEvent(
      cleanState(),
      DeleteFriend(1)
    )
    assertEquals(0, state.friends.size)
  }

  test("testChangeFriendAlias") {
    val state = acceptEvent(
      cleanState(),
      ChangeFriendAlias(1, "big cony")
    )
    assertEquals("big cony", state.friends(1).alias)
  }

  test("testChangeGroupAlias") {
    val state = acceptEvent(
      cleanState(),
      ChangeGroupAlias(1, "line town")
    )
    assertEquals("line town", state.groups(1).alias)
  }

  test("testCreateGroup") {
    val state = acceptEvent(
      cleanState(),
      CreateGroup("alien", "common")
    )
    assertEquals(2, state.groups.size)
  }

  test("testSendPrivateMessage") {
    val state = acceptEvent(
      cleanState(),
      SendPrivateMessage(1, Message("common", 1, "cony", "unread"))
    )
    assertEquals("common", state.friends(1).conversation.messages(1).messageType)
    assertEquals(1, state.friends(1).conversation.messages(1).timestamp)
    assertEquals("cony", state.friends(1).conversation.messages(1).content)
    assertEquals("unread", state.friends(1).conversation.messages(1).status)
  }

  test("testSendPublicMessage") {
    val state = acceptEvent(
      cleanState(),
      SendPublicMessage(1, Message("common", 1, "cony", "unread"))
    )
    assertEquals("common", state.groups(1).conversation.messages(0).messageType)
    assertEquals(1, state.groups(1).conversation.messages(0).timestamp)
    assertEquals("cony", state.groups(1).conversation.messages(0).content)
    assertEquals("unread", state.groups(1).conversation.messages(0).status)
  }

  test("testChangeFriendStatus") {
    val state = acceptEvent(
      cleanState(),
      ChangeFriendStarStatus(1)
    )
    assertEquals("Star/unstar", state.friends(1).isStarred)
  }

  test("testChangeGroupStarStatus") {
    val state = acceptEvent(
      cleanState(),
      ChangeGroupStarStatus(1)
    )
    assertEquals("Star/unstar", state.groups(1).isStarred)
  }

}
