package im.tox.hlapi

import java.io.{File, FileInputStream, FileOutputStream}

import com.typesafe.scalalogging.Logger
import im.tox.client.proto.Profile
import im.tox.hlapi.Client.ToxClientConnection
import im.tox.tox4j.core.ToxCore
import im.tox.tox4j.core.data.{ToxNickname, ToxPublicKey, ToxStatusMessage}
import im.tox.tox4j.impl.jni.ToxCoreEventDispatch
import org.slf4j.LoggerFactory

import scalaz.stream._
import scalaz.{-\/, \/-}

@SuppressWarnings(Array("org.brianmckenna.wartremover.warts.Any"))
case object ProfileManager {

  private val logger = Logger(LoggerFactory.getLogger(getClass))

  private val savePath = Seq(new File("tools/toxsaves"), new File("projects/tox4j/tools/toxsaves")).find(_.exists)

  def saveProfile(profile: Profile): Process[ToxClientConnection, Unit] = {
    Client.ask.map {
      case (tox, av) =>
        saveProfile(tox, profile)
    }
  }

  private def saveProfile(tox: ToxCore, profile: Profile): Unit = {
    savePath.foreach { savePath =>
      val output = new FileOutputStream(new File(savePath, tox.getPublicKey.toHexString))
      try {
        profile.writeTo(output)
        logger.info(s"Saved profile for ${tox.getPublicKey}")
      } finally {
        output.close()
      }
    }
  }

  def loadProfile(id: Int): Process[ToxClientConnection, Profile] = {
    Client.ask.flatMap[ToxClientConnection, Profile] {
      case (tox, av) =>
        loadProfile(id, tox)
          .map(Client.emit)
          .getOrElse(createProfile(id, tox))
    }
  }

  private def createProfile(id: Int, tox: ToxCore): Process[ToxClientConnection, Profile] = {
    val profile = Profile(
      name = tox.getName.toString,
      statusMessage = tox.getStatusMessage.toString,
      nospam = tox.getNospam,
      status = ToxCoreEventDispatch.convert(tox.getStatus),
      friendKeys = tox.getFriendNumbers.map(tox.getFriendPublicKey).map(_.toHexString)
    )
    logger.info(s"[$id] Created new profile for ${tox.getPublicKey}")
    saveProfile(profile).map(_ => profile)
  }

  private def loadProfile(id: Int, tox: ToxCore): Option[Profile] = {
    savePath.map { savePath =>
      val input = new FileInputStream(new File(savePath, tox.getPublicKey.toHexString))
      try {
        val profile = Profile.parseFrom(input)
        tox.setName(ToxNickname(profile.name.getBytes))
        tox.setStatusMessage(ToxStatusMessage(profile.statusMessage.getBytes))
        tox.setNospam(profile.nospam)
        tox.setStatus(ToxCoreEventDispatch.convert(profile.status))

        logger.info(s"[$id] Adding ${profile.friendKeys.length} friends from saved friend list")
        profile.friendKeys.foreach { hexKey =>
          logger.debug(s"[$id] - $hexKey")
          ToxPublicKey.fromHexString(hexKey) match {
            case -\/(failure) =>
              logger.error(s"[$id] Invalid key ($failure): $hexKey")
            case \/-(key) =>
              tox.addFriendNorequest(key)
          }
        }

        logger.info(s"[$id] Successfully read profile for ${tox.getAddress}")
        profile
      } finally {
        input.close()
      }
    }
  }
}
