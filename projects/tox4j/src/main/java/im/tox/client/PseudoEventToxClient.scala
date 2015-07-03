package im.tox.client

/*
  The event class that every client will wrap request into
  Can be broken to more hierachy later
 */
sealed trait ToxEvent

case class Login(username: String, password: String) extends ToxEvent

case class GroupConversation(conversation: String) extends ToxEvent
case class PrivateConversation(conversation: String) extends ToxEvent
case class ConversationList() extends ToxEvent

case class GetGroupConversation(val conversation: GroupConversation, val operation: GetEntity) extends ToxEvent

case class Friend(friend: String) extends ToxEvent
case class FriendList() extends ToxEvent

case class GetFriendList(val friendList: FriendList, val operation: GetEntity) extends ToxEvent

case class SetAttribute(option: String) extends ToxEvent
case class FillAttribute(input: String) extends ToxEvent
case class GetAttribute(attribute: String) extends ToxEvent
case class GetEntity() extends ToxEvent
case class CreateEntity() extends ToxEvent

object ToxEventAdaptor {
  //Method defined in HLAPI, return the result of the event or none
  //Can be broken into more specific functions later
  def passToxEvent(event: ToxEvent): Option[String] = {
    None
  }
}

abstract class PseudoEventToxClient {

  def login(): Unit = {
    ToxEventAdaptor.passToxEvent(new Login("brown", "12345"))
  }

  def startGroupConversation(): Unit = {
    val friendList = ToxEventAdaptor.passToxEvent(new GetFriendList(new FriendList(), GetEntity()))
    ToxEventAdaptor.passToxEvent(new GetGroupConversation(new GroupConversation("null"), new GetEntity()))
  }
  
}

