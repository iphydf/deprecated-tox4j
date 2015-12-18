package im.tox.hlapi

import java.util
import java.util.concurrent.ScheduledThreadPoolExecutor

import com.typesafe.scalalogging.Logger
import im.tox.client.ToxClientState
import im.tox.hlapi.Client.ToxClientConnection
import im.tox.tox4j.OptimisedIdOps._
import im.tox.tox4j.av.callbacks.audio.AudioPlayback
import im.tox.tox4j.av.data._
import im.tox.tox4j.av.enums.ToxavFriendCallState
import im.tox.tox4j.core.data._
import im.tox.tox4j.core.enums.ToxMessageType
import org.slf4j.LoggerFactory

import scala.language.postfixOps
import scalaz.Lens
import scalaz.stream.{Process, time}

/**
 * Handles audio/video calls.
 */
case object AudioVideoEventListener extends EventProcessor[ToxClientState] {

  private val logger = Logger(LoggerFactory.getLogger(getClass))

  private lazy val playback = new AudioPlayback(SamplingRate.Rate48k, AudioChannels.Stereo, 128)

  private val audioBitRate = BitRate.fromInt(320).get
  private val audioLength = AudioLength.Length60
  private val audioSamplingRate = SamplingRate.Rate8k
  private val audioFrameSize = (audioLength.value.toMillis * audioSamplingRate.value / 1000).toInt
  private val audioFramesPerIteration = 1

  private val videoBitRate = BitRate.fromInt(1).get

  override def friendMessage(
    friendNumber: ToxFriendNumber,
    messageType: ToxMessageType,
    timeDelta: Int,
    message: ToxFriendMessage
  )(state: ToxClientState): Process[ToxClientConnection, ToxClientState] = {
    val command = message.toString.toLowerCase
    command match {
      case "call me" =>
        Client.privateAccess { (tox, av) =>
          logger.info(s"Ringing ${state.friends(friendNumber).name}")
          av.call(friendNumber, audioBitRate, videoBitRate)
          state
        }

      case _ =>
        Process.emit(state)
    }
  }

  override def call(
    friendNumber: ToxFriendNumber,
    audioEnabled: Boolean,
    videoEnabled: Boolean
  )(state: ToxClientState): Process[ToxClientConnection, ToxClientState] = {
    Client.privateAccess { (tox, av) =>
      logger.info(s"Answering call from $friendNumber")
      av.answer(friendNumber, audioBitRate, videoBitRate)

      av.invokeCallState(friendNumber, util.EnumSet.of(
        ToxavFriendCallState.ACCEPTING_A,
        ToxavFriendCallState.ACCEPTING_V
      ))

      state
    }
  }

  override def callState(
    friendNumber: ToxFriendNumber,
    callState: util.EnumSet[ToxavFriendCallState]
  )(state: ToxClientState): Process[ToxClientConnection, ToxClientState] = {
    for {
      state <- startStopAudioSending(friendNumber, callState)(state)
      state <- startStopVideoSending(friendNumber, callState)(state)
    } yield {
      state
    }
  }

  private def startStopAudioSending(
    friendNumber: ToxFriendNumber,
    callState: util.Collection[ToxavFriendCallState]
  )(state: ToxClientState): Process[ToxClientConnection, ToxClientState] = {
    val audioTime = ToxClientState.friendAudioTime(friendNumber)

    if (callState.contains(ToxavFriendCallState.ACCEPTING_A)) {
      logger.info(s"Sending audio to friend $friendNumber")
      sendNextAudioFrame(friendNumber, audioTime.set(state, Some(0)))
    } else {
      Process.emit(audioTime.mod({
        case None => None
        case Some(_) =>
          logger.info(s"Stopped sending audio to friend $friendNumber")
          None
      }, state))
    }
  }

  private def sendNextAudioFrame(friendNumber: ToxFriendNumber, state: ToxClientState): Process[ToxClientConnection, ToxClientState] = {
    val audioTime = ToxClientState.friendAudioTime(friendNumber)

    // Get next audio frames and send them.
    val audio = ToxClientState.friendAudio(friendNumber).get(state)

    Client.privateAccessFlat { (tox, av) =>
      Client.lift {
        implicit val sc: ScheduledThreadPoolExecutor = new java.util.concurrent.ScheduledThreadPoolExecutor(1)
        time.awakeEvery(audioLength.value).fold(0) { (t, _) =>
          println(s"OMG: $t")
          val pcm = audio.nextFrame16(audioLength, audioSamplingRate, t)
          av.audioSendFrame(
            friendNumber,
            pcm,
            SampleCount(audioLength, audioSamplingRate),
            AudioChannels.Mono,
            audioSamplingRate
          )
          t + audioFrameSize
        }
      }
    }.drain
  }

  private def startStopVideoSending(
    friendNumber: ToxFriendNumber,
    callState: util.Collection[ToxavFriendCallState]
  )(state: ToxClientState): Process[ToxClientConnection, ToxClientState] = {
    val videoFrame = ToxClientState.friendVideoFrame(friendNumber)

    if (callState.contains(ToxavFriendCallState.ACCEPTING_V)) {
      logger.info(s"Sending video to friend $friendNumber")
      sendNextVideoFrame(friendNumber)(videoFrame.set(state, Some(0)))
    } else {
      Process.emit(videoFrame.mod({
        case None => None
        case Some(_) =>
          logger.info(s"Stopped sending video to friend $friendNumber")
          None
      }, state))
    }
  }

  private def sendNextVideoFrame(friendNumber: ToxFriendNumber)(state: ToxClientState): Process[ToxClientConnection, ToxClientState] = {
    val videoFrame = ToxClientState.friendVideoFrame(friendNumber)

    videoFrame.get(state) match {
      case None =>
        Process.emit(state) // finished
      case Some(t) =>
        // Get next frame and send it.
        val video = ToxClientState.friendVideo(friendNumber).get(state)
        val (y, u, v) = video.yuv(t)
        for {
          () <- Client.privateAccess { (tox, av) =>
            av.videoSendFrame(friendNumber, video.width.value, video.height.value, y, u, v)
          }
          state <- sendNextVideoFrame(friendNumber)(videoFrame.set(state, Some(t + 1)))
        } yield {
          state
        }
    }
  }

  override def bitRateStatus(
    friendNumber: ToxFriendNumber,
    audioBitRate0: BitRate,
    videoBitRate0: BitRate
  )(state: ToxClientState): Process[ToxClientConnection, ToxClientState] = {
    val audioBitRate = audioBitRate0.copy(audioBitRate0.value max 8)
    val videoBitRate = videoBitRate0.copy(videoBitRate0.value max 1)

    Client.privateAccess { (tox, av) =>
      av.setBitRate(friendNumber, audioBitRate, videoBitRate)

      val audioTime = ToxClientState.friendAudioTime(friendNumber)
      val videoFrame = ToxClientState.friendVideoFrame(friendNumber)

      (state
        |> setBitRate("audio", audioTime, audioBitRate)
        |> setBitRate("video", videoFrame, videoBitRate))
    }
  }

  private def setBitRate(
    target: String,
    lens: Lens[ToxClientState, Option[Int]],
    bitRate: BitRate
  )(state: ToxClientState): ToxClientState = {
    if (bitRate == BitRate.Disabled) {
      logger.info(s"Stopping $target sending")
      lens.set(state, None)
    } else {
      // If the frame number was already set, leave it be, otherwise set it to 0.
      lens.mod(_.orElse(Some(0)), state)
    }
  }

  override def audioReceiveFrame(
    friendNumber: ToxFriendNumber,
    pcm: Array[Short],
    channels: AudioChannels,
    samplingRate: SamplingRate
  )(state: ToxClientState): Process[ToxClientConnection, ToxClientState] = {
    playback.play(pcm)
    Process.emit(state)
  }

  override def videoReceiveFrame(
    friendNumber: ToxFriendNumber,
    width: Width, height: Height,
    y: Array[Byte], u: Array[Byte], v: Array[Byte],
    yStride: Int, uStride: Int, vStride: Int
  )(state: ToxClientState): Process[ToxClientConnection, ToxClientState] = {
    Process.emit(state)
  }

}
