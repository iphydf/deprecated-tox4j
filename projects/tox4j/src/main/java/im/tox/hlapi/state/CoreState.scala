package im.tox.hlapi.state

import im.tox.hlapi.state.ConnectionState._
import im.tox.hlapi.state.FriendState.FriendList
import im.tox.hlapi.state.PublicKeyState.{ Address, PublicKey }
import im.tox.hlapi.state.UserProfileState.UserProfile
import im.tox.hlapi.state.UserStatusState.{ Online, UserStatus }

import scalaz.Lens

object CoreState {

  val userProfileL = Lens.lensu[ToxState, UserProfile](
    (a, value) => a.copy(userProfile = value),
    _.userProfile
  )

  val publicKeyL = Lens.lensu[ToxState, PublicKey](
    (a, value) => a.copy(publicKey = value),
    _.publicKey
  )

  val userStatusL = Lens.lensu[ToxState, UserStatus](
    (a, value) => a.copy(userStatus = value),
    _.userStatus
  )

  val friendListL = Lens.lensu[ToxState, FriendList](
    (a, value) => a.copy(friendList = value),
    _.friendList
  )

  val addressL = Lens.lensu[ToxState, Address](
    (a, value) => a.copy(address = value),
    _.address
  )

  val stateNicknameL = userProfileL >=> UserProfileState.nicknameL
  val stateStatusMessageL = userProfileL >=> UserProfileState.statusMessageL
  val friendsL = friendListL >=> FriendState.friendListFriendsL

  final case class ToxState(
    userProfile: UserProfile = UserProfile(),
    userStatus: UserStatus = Online(),
    friendList: FriendList = FriendList(),
    publicKey: PublicKey = PublicKey(),
    address: Address = Address()
  )

}
