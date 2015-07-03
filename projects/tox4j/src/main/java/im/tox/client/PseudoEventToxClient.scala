package im.tox.client

import javax.smartcardio.ATR

/*
  The event class that every client will wrap request into
  Can be broken to more hierachy later
 */
sealed trait ToxOperation
sealed trait ToxEntity
sealed trait ToxAttribute
sealed trait ToxEvent

case class Login(username: String, password: String) extends ToxEvent

/*
  Entities
 */
case class GroupConversation(identifier: String) extends ToxEntity
case class PrivateConversation(identifier: String) extends ToxEntity
case class GroupConversationList() extends ToxEvent
case class PrivateConversationList() extends ToxEvent
case class Friend(friend: String) extends ToxEntity
case class FriendList() extends ToxEntity

case class Message(identifier: String) extends ToxEntity
case class File(identifier: String) extends ToxEntity
case class AudioCall(identifer: String) extends ToxEntity
/*
  Attributes
 */
case class Alias(entity: ToxEntity) extends ToxAttribute
//This should be separated from attributes later
case class FileList(entity: ToxEntity) extends ToxAttribute
case class isMute(entity: ToxEntity) extends ToxAttribute
/*
  Operations
 */
//These should be categorized into more specific case classes later
case class SetAttribute(option: String) extends ToxOperation
case class FillAttribute(input: String) extends ToxOperation
case class GetAttribute() extends ToxOperation
case class GetEntity() extends ToxOperation
case class CreateEntity(param: String) extends ToxOperation
case class LeaveEntity() extends ToxOperation
case class DestroyEntity() extends ToxOperation
case class BlockEntity() extends ToxOperation
case class StarEntity() extends ToxOperation
case class EditMessage(param: String) extends ToxOperation
case class RecallMessage() extends ToxOperation
case class AddTo() extends ToxOperation
case class SendTo() extends ToxOperation
case class GetMembers() extends ToxOperation

//Bad naming
case class SingleEvent(val subject: ToxEntity, val operation: ToxOperation) extends ToxEvent
case class DoubleEvent(val subject: ToxEntity, val objec: ToxEntity, val operation: ToxOperation) extends ToxEvent
case class AttributeEvent(val attribute: ToxAttribute, val operation: ToxOperation) extends ToxEvent

object ToxEventAdaptor {
  //Method defined in HLAPI, return the result of the event or none
  //Can be broken into more specific functions later
  def passToxEvent(event: ToxEvent): Option[String] = {
    None
  }
}

abstract class PseudoEventToxClient {

  def login(): Unit = {
    ToxEventAdaptor.passToxEvent(Login("brown", "12345"))
  }

  def startGroupConversation(): Unit = {
    val friendList = ToxEventAdaptor.passToxEvent(SingleEvent(FriendList(), GetEntity()))
    ToxEventAdaptor.passToxEvent(SingleEvent(GroupConversation("null"), CreateEntity("friendlist")))
  }

  def leaveGroupConversation(): Unit = {
    val allGroups = ToxEventAdaptor.passToxEvent(SingleEvent(GroupConversation("brown"), GetEntity()))
    //Client display the group conversation list
    //for each of the group show its name and icon
    //client selects a list of groups that it wants to leave
    ToxEventAdaptor.passToxEvent(SingleEvent(GroupConversation("brown"), LeaveEntity()))
  }

  def inviteFriendToGroupChat(): Unit = {
    ToxEventAdaptor.passToxEvent(DoubleEvent(Friend("cony"), GroupConversation("brown"), AddTo()))
  }

  def startPrivateConversationWithGroupMember(): Unit = {
    val memberlist = ToxEventAdaptor.passToxEvent(SingleEvent(GroupConversation("brown"), GetMembers()))
    //client displays the member list
    ToxEventAdaptor.passToxEvent(SingleEvent(PrivateConversation("null"), CreateEntity("brown")))
  }

  def changeGroupAlias(): Unit = {
    ToxEventAdaptor.passToxEvent(AttributeEvent(Alias(GroupConversation("brown")), FillAttribute("brownie")))
  }

  def sendMessage(): Unit = {
    ToxEventAdaptor.passToxEvent(DoubleEvent(PrivateConversation("brown"), Message("I'm cony"), SendTo()))
  }

  def deleteMessage(): Unit = {
    ToxEventAdaptor.passToxEvent(SingleEvent(Message("brown message"), DestroyEntity()))
  }

  def editMessage(): Unit = {
    ToxEventAdaptor.passToxEvent(SingleEvent(Message("brown message"), EditMessage("cony message")))
  }

  def recallMessage(): Unit = {
    ToxEventAdaptor.passToxEvent(SingleEvent(Message("brown message"), RecallMessage()))
  }

  def sendFile(): Unit = {
    ToxEventAdaptor.passToxEvent(DoubleEvent(File("brown file"), PrivateConversation("brown"), SendTo()))
  }

  def getConversationFiles(): Unit = {
    ToxEventAdaptor.passToxEvent(AttributeEvent(FileList(PrivateConversation("brown")), GetAttribute()))
  }

  def muteConversation(): Unit = {
    ToxEventAdaptor.passToxEvent(AttributeEvent(isMute(PrivateConversation("brown")), SetAttribute("mute")))
  }

  def addFriend(): Unit = {
    ToxEventAdaptor.passToxEvent(DoubleEvent(Friend("brown"), FriendList(), AddTo()))
  }

  def changeFriendAlias: Unit = {
    ToxEventAdaptor.passToxEvent(AttributeEvent(Alias(Friend("brown")), FillAttribute("cony")))
  }

  def deleteFriend: Unit = {
    ToxEventAdaptor.passToxEvent(SingleEvent(Friend("brown"), DestroyEntity()))
  }

  def blockFriend: Unit = {
    ToxEventAdaptor.passToxEvent(SingleEvent(Friend("brown"), BlockEntity()))
  }

  def startFriend: Unit = {
    ToxEventAdaptor.passToxEvent(SingleEvent(Friend("brown"), StarEntity()))
  }

  def initialAudioCall: Unit = {
    ToxEventAdaptor.passToxEvent(DoubleEvent(AudioCall("hello cony"), PrivateConversation("brown"), SendTo()))
  }

}

