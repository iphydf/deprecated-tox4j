package im.tox.client.hlapi

class State {
  final case class ToxState() {
    case class UserStatus()
    case class ConnectionStatus()
    case class FriendList() {
      Seq(User)
    }
    case class ConversationList() {
      Seq(Conversation)
    }
    case class Profile() {
      case class Nickname()
      case class ProfilePhoto()
    }
    case class User() {
      case class Alias()
      case class Blocked()
      Profile
    }
    case class Conversation() {

      case class Starred()
      case class Muted()
      case class MessageList() {
        Seq(Message)
      }
      case class FileTransmissionList() {
        Seq(FileRecord)
      }

    }
    case class PrivateConversation() extends Conversation()
    case class GroupConversation() extends Conversation()
    case class TransmissionStatus()
    case class Message() {
      case class TimeStamp()
      case class Context()
      final case class MessageTransmissionStatus() extends TransmissionStatus()
    }
    case class FileRecord() {
      case class TimeStamp()
      case class File()
      final case class FileTransmissionStatus() extends TransmissionStatus()
    }

  }

}