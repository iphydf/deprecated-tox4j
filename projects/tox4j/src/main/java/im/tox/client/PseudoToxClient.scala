package im.tox.client

import scalaz.\/


abstract class PseudoToxClient {
  /**
   * This should be already carried with all the information loaded locally and retrieved from server when login.
   * For all the operations that require the modification of settings and profiles, use lens to set the ToxInstance
   * (or will an OO way make it easier? just for this specific object)
   * Make it a global variable so that we do not need to pass it everytime?
   */
  val user: String

  /*
    Login and registration
   */
  def createToxInstance(username: String, password: String): \/[String, String]
  def registerIncomingMessage(func:(String, String) => Unit): Option[String]
  def registerIncomingAudioCall(func:(String, String) => Unit): Option[String]
  def registerFriendRequest(func:(String, String) => Unit): Option[String]
  def registerFileTransferRequest(func:(String, String) => Unit): Option[String]

  /*
    User profile related functions
   */
  //General call for setting user's attribute that can be selected from options,
  // can use state monad here
  def setProfile(option: String, operation: String => Option[String])
  //callbacks for series of atrributes
  def setStatus(option: String): Option[String]
  //General call for setting user's attribute that need to be filled in
  def fillProfile(input: String, operation: String => Option[String])
  //callbacks for a series of attributes
  def fillNickName(input: String): Option[String]
  def fillDefaultRejectedCallMessages(input: String): Option[String]

  /*
    Friend related functions
   */
  //Get all firends of a user
  def getAllFriends(): String
  def addFriend(friendId: String): Option[String]
  def friendOperation(friendId: String, operation: String => Option[String]): Option[String]
  //callbacks on operation on friends
  def deleteFriend(friendId: String): Option[String]
  // For the following two, as they are manipulating on boolean attributes
  //can use state monad inside
  def blockFriend(friendId: String): Option[String]
  def starFriend(friendId: String): Option[String]
  //These functions are for setting the attibutes of a friend that need input from the user
  def fillFriendAttribute(friendId: String, attribute: String, operation: (String, String) => Option[String]): Option[String]
  //callback for changing the alias
  def fillFriendAlias(friendId: String, newAlias: String): Option[String]

  /*
    Conversation related functions
   */
  //Get all conversations, sorted by time
  def getAllConversations(): \/[String, String]
  //Get all private conversations, sorted by time
  def getAllPrivateConversations(): \/[String, String]
  //Get all group conversations, sorted by time
  def getAllGroupConversations(): \/[String, String]

  //Get all group conversations of a user
  def getGroupConversationsList(): Array[String]

  //Either return the error or a new group conversation
  def createGroupConversation(friendList: Array[String], Option: String): \/[String, String]
  def deleteConversation(conversation: String): Option[String]

  //Return error code or success on None
  def leaveGroupConversation(GroupConversations: Array[String]): Option[String]

  //Get all members of a group
  def getGroupConversationMemberList(): Array[String]

  // Operations on the add/delete of a group member. Permission checking is done in this function.
  // Return either error code or the newly updated conversation
  def updateGroupMembers(GroupConversation: String, members: Array[String], operation: (String, Array[String]) => \/[String, String]): \/[String, String]
  /** Callbacks for operation of adding/deleting friends to group chat
    *Return either the error of the updated conversation
    */
  // Permission check inside, need to be friend
  def addFriendsToGroupConversation(GroupConversation: String, members: Array[String]): \/[String, String]
  // Permission check inside, need to be some kind of administrator of the group
  def deleteMembersFromGroupConversation(GroupConversation: String, members: Array[String]): \/[String, String]
  // Set group conversation attribute
  def setGroupConversationAttribute(GroupConversation: String, attribute: String, operation:(String, String) => \/[String, String]): \/[String, String]
  //Change group alias
  def setAlias(GroupConversation: String, attribute: String): \/[String, String]

  //Get a specific private conversation with a friend
  def getPrivateConversation(friend: String): \/[String, String]
  // The conversation should contains a collection of messages that has been sent and received
  // When a new message is sent, it should be appended to this collection (done by HLAPI)
  //@return the conversation after updated
  def updateConversationMessages(conversatioin: String, messages: Array[String], operation:(String, Array[String]) => Option[String]): String
  //callbacks for conversation updates
  def deleteMessages(conversation: String, messages: Array[String]): Option[String]
  // This should only call privately by HLAPI
  def addMessages(conversation: String, messages: Array[String]): Option[String]
  // Permission check should be done in this process
  def recallMessages(conversation: String, messages: Array[String]): Option[String]
  //These are to get the attributes of messages, can use lens inside
  def getMessageStatus(conversation: String, message: String): Option[String]

  //function for editing a message of a conversation
  def editMessage(conversation: String, message: String): \/[String, String]

  def sendMessage(Conversation: String, message: String): Option[String]
  //
  def loadChatHistory(Conversation: String, numToLoad: Int): \/[String, Array[String]]
  //Each conversation has a collection of file record associated with it
  //The option can be all records, sucess records, fail records..etc
  def getConversationFileList(Conversation: String, option: String): \/[String, String]

  //These series of functions are for setting a single attribute of a conversation
  //State monad can be used inside
  def setConversationAttribute(Conversation: String, option: String, operation:(String, String) => Option[String]): Option[String]
  //callback for setting the mute of a conversation
  def setMute(conversation: String, option: String): Option[String]

  /**
    * File related functions
   */
  // This function either returned on error (connection lost etc.)
  // or return the final status of file transfer (accepted all, friend reject, pause)
  def sendFile(privateConversation: String, fileData: String): \/[String, String]

  /**
    *  AV related
   */
  def initiateVideoRequest(friend: String): \/[String, String]

  def login(userName: String, password: String): Unit = {
    //user = createToxInstance(userName, password)
    //Register callbacks with predefined functions by client
    //registerNewMessage(callback1)
    //registerIncomingCall(callback2)
    //blah blah
  }
  /**
    *  Start a group chat
   */
  // Search for friend list
  def startGroupConversation(): Unit = {
    val friendList = getAllFriends()
    //After user selected the friends to be added
    createGroupConversation(Array.ofDim[String](0), "blah")
  }

  /**
    * Leave a group chat
   */
  def leaveGroupChat(): Unit = {
    val allGroups = getGroupConversationsList()
    //Client display the group conversation list
    //for each of the group show its name and icon
    //client selects a list of groups that it wants to leave
    leaveGroupConversation(Array.ofDim[String](0))
  }

  /**
    * Invite a friend to a group chat
   */
  //Click the option "See Group Members"
  def inviteFriendToGroupChat(): Unit = {
    //client display the friend list and user selects one
    updateGroupMembers("Group blah", Array.ofDim[String](0), addFriendsToGroupConversation)
  }

  /**
    *Start a private conversation with a group member (if the user is a friend)
   */
  def startPrivateConversationWithAGroupMember(): Unit = {
    //Client display conversation members and user selects one
    //HLAPI will see if this there is already a conversation with this friend, if there is, load it; if not, create it.
    //Permission checking can be handled in two places.
    // HLAPI will provide permission checking function, and this function will also be called in sendMessage.
    // For client who would like to exclude non-friends from the displayed list,
    // it could check the permission state first, or it could diaplay error message after selecting the friend,
    // which is triggered after getConversation
    val conversation = getPrivateConversation("brown")
    sendMessage("brown group", "blah")
  }

  /*
  * Change a group chat alias
  */
  def changeGroupAlias(): Unit = {
    //Actually this should be done whenever switch to the inbox view
    val conversations = getAllConversations()
    // client display it and the user chooses one
    setGroupConversationAttribute("cony group", "brown group", setAlias)
  }

  /**
    *Send a message to a friend
   */
  def sendMessage(): Unit = {
    sendMessage("brown", "blah")
  }

  /**
    *Send a file to a conversation (friend)
   */
  // File transmission should use stream and future (not so familiar with that now, so do not include much details).
  // This function should be called asynchronously
  def transferFile(): Unit = {
    //Client let user select conversation and select a file,
    //which is turned into stream by the client
    sendFile("brown", "I'm the stream read from file")
  }

  /*
  *Delete a single / multiple message
  */
  def deleteMessage(): Unit = {
    //Actually these should also done in previous cases that display conversations views, specify here.
    //The client decides to load how many past messages when first loaded
    loadChatHistory("brown", 10)
    //Get the thumbnail photos of each speaker,
    // if it is not groupchat it can be called at the first place when the conversation is first loaded, i
    // f it is groupchat, done in the way below.
    // Another way is to discriminate between the group chat message and private chat message to reduce duplicated data
    //The following are for display, client might as well wrap these into a function like displayConversationDetail(Conversation)
    //foreach(message: Message)(loadThumbnail: () => ThumbnailId): ThumbnailId
    //foreach(message: Message)(loadContent: () => TextMessage): TextMessage / Array[Byte] / String （ dont know which level is better)
    updateConversationMessages("brown", Array.ofDim[String](0), deleteMessages)
  }

  /**
    *Edit a message
    */
  def editMessage(): Unit = {
    //Same as the delete before
    editMessage("brown", "ben")
  }

  /*
  * Recall a message
  */
  def recallMessages(): Unit = {
    //Recall here is more complicated. In wechat you are not allowed to recall a message after two minutes of sending it.
    // But I don't think that makes sense, as we have receipt here you can recall whenever the message is unread.
    // Tricky cases can be caused by the slight latency before actually seen and receive the receipt.
    // So the permission checking can be either done in once or twice
    //Same as delete and edit before
    //This step can be done or not, as recall itself can return the status, depends on what user experience the client wants.
    // This can be done by lens?
    getMessageStatus("brown", "ben message")
    updateConversationMessages("brown", Array.ofDim[String](0), recallMessages)

  }

    /**
      * See conversation files list
      */
  def getConversationFiles(): Unit = {
      getConversationFileList("brown", "fail")
    }

  /**
    * Mute a conversation
    */
  def muteConversation(): Unit = {
    //Same loading as above
    setConversationAttribute("brown", "MUTE", setMute)
  }

  /**
    * Save a picture from a chat to SD card
    */
  //Don't know about the android stuff :( A user implemented interface should be defined here
  def savePicture(): Unit = {

  }

  /**
    *Add a friend
    */
  def addFriend(): Unit = {
    //Add a friend with ID
    addFriend("brown")
  }
  /*
  *Change a friend's alias
  */
  def changeFriendAlias(): Unit = {
    fillFriendAttribute("brown", "brownie", fillFriendAlias)
  }

  /*
  *Save a friend's profile photo to the phone memory
  */
  //Dont know about the image thing again :( should deine the interface for photo saving later
  def saveFriendProfilePhoto(): Unit = {

  }

  /*
  *Delete a friend
  */
  def deleteAFriend(): Unit = {
    //Get the friend id as before
    friendOperation("james", deleteFriend)
  }

  /*
  *Block a friend
  */
  def blockAFriend(): Unit = {
    //Get the friend id as before
    friendOperation("james", blockFriend)
  }

  /*
  *Star a friend
  */
  def starAFriend(): Unit = {
    friendOperation("james", starFriend)
  }

  /*
  * Change friend color
  */
  // Should it better be done in the client?
  def changeFriendColor: Unit = {

  }

  /*
  * Make an audio / video call
  */
  def initialAVCall(): Unit = {
    // load friend as before
    // This should be asynchronous
    initiateVideoRequest("brown")
    // Not familiar with the AV stuffs :(
  }

  /*
  * See files send by the contact
  */
  //Same with the filelist before

  /*
  *Delete a conversation
  */
  def deleteConversations(): Unit = {
    //load conversation list as before
    deleteConversation("brown")
  }

  /*
  *Search a conversation
  */
  def searchConversation(): Unit = {
    //The client should load the conversation list and search for it by itself
  }

}
