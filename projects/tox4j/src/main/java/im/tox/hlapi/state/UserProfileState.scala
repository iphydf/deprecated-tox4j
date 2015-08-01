package im.tox.hlapi.state

import scalaz.Lens

object UserProfileState {

  final case class UserProfile(nickname: Array[Byte] = Array.ofDim(0), statusMessage: Array[Byte] = Array.ofDim(0))

  val nicknameL = Lens.lensu[UserProfile, Array[Byte]](
    (a, value) => a.copy(nickname = value),
    _.nickname
  )

  val statusMessageL = Lens.lensu[UserProfile, Array[Byte]](
    (a, value) => a.copy(statusMessage = value),
    _.statusMessage
  )
}
