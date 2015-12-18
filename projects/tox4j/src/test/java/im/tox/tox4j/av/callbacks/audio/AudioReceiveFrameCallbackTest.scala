package im.tox.tox4j.av.callbacks.audio

import java.util

import com.typesafe.scalalogging.Logger
import im.tox.tox4j.av.ToxAv
import im.tox.tox4j.av.data._
import im.tox.tox4j.av.enums.ToxavFriendCallState
import im.tox.tox4j.core.ToxCore
import im.tox.tox4j.core.data.ToxFriendNumber
import im.tox.tox4j.core.enums.ToxConnection
import im.tox.tox4j.testing.ToxExceptionChecks
import im.tox.tox4j.testing.autotest.AutoTestSuite
import org.slf4j.LoggerFactory

final class AudioReceiveFrameCallbackTest extends AutoTestSuite with ToxExceptionChecks {

  private val logger = Logger(LoggerFactory.getLogger(getClass))

  override def maxParticipantCount: Int = 2

  type S = Int

  object Handler extends EventListener(0) {

    val bitRate = BitRate.fromInt(320).get
    val channels = AudioChannels.Mono
    val audioLength = AudioLength.Length40
    val samplingRate = SamplingRate.Rate48k
    val frameSize = SampleCount(audioLength, samplingRate).value
    val framesPerIteration = 2

    val audio = AudioGenerators.default
    val playback = new AudioPlayback(samplingRate, channels, audio.length(samplingRate) / 2)
    val displayWave = !sys.env.contains("TRAVIS")

    override def friendConnectionStatus(
      friendNumber: ToxFriendNumber,
      connectionStatus: ToxConnection
    )(state0: State): State = {
      val state = super.friendConnectionStatus(friendNumber, connectionStatus)(state0)

      if (connectionStatus == ToxConnection.NONE || state.id(friendNumber) != state.id.next) {
        state
      } else {
        // Call id+1.
        state.addTask { (tox, av, state) =>
          debug(state, s"Ringing ${state.id(friendNumber)}")
          av.call(friendNumber, bitRate, BitRate.Disabled)
          state
        }
      }
    }

    override def call(friendNumber: ToxFriendNumber, audioEnabled: Boolean, videoEnabled: Boolean)(state: State): State = {
      if (state.id(friendNumber) == state.id.prev) {
        state.addTask { (tox, av, state) =>
          debug(state, s"Got a call from ${state.id(friendNumber)}; accepting")
          av.answer(friendNumber, BitRate.Disabled, BitRate.Disabled)
          state
        }
      } else {
        fail(s"I shouldn't have been called by friend ${state.id(friendNumber)}")
        state
      }
    }

    private def sendFrame(friendNumber: ToxFriendNumber)(tox: ToxCore, av: ToxAv, state0: State): State = {
      val state = state0.modify(_ + frameSize * framesPerIteration)

      for (t <- state0.get until state.get by frameSize) {
        val pcm = audio.nextFrame16(audioLength, samplingRate, t)
        av.audioSendFrame(
          friendNumber,
          pcm,
          SampleCount(audioLength, samplingRate),
          channels,
          samplingRate
        )
      }

      if (state.get >= audio.length(samplingRate)) {
        state.finish
      } else {
        state.addTask(sendFrame(friendNumber))
      }
    }

    override def callState(friendNumber: ToxFriendNumber, callState: util.EnumSet[ToxavFriendCallState])(state: State): State = {
      debug(state, s"Call with ${state.id(friendNumber)} is now $callState")
      assert(callState == util.EnumSet.of(
        ToxavFriendCallState.ACCEPTING_A,
        ToxavFriendCallState.ACCEPTING_V
      ))
      state.addTask(sendFrame(friendNumber))
    }

    override def bitRateStatus(friendNumber: ToxFriendNumber, audioBitRate: BitRate, videoBitRate: BitRate)(state: State): State = {
      debug(state, s"Bit rate in call with ${state.id(friendNumber)} should change to $audioBitRate for audio and $videoBitRate for video")
      state
    }

    def waitForPlayback(length: Int)(state: State): State = {
      if (!playback.done(audio.length(samplingRate))) {
        state.addTask { (tox, av, state) =>
          waitForPlayback(length)(state)
        }
      } else {
        state.finish
      }
    }

    override def audioReceiveFrame(
      friendNumber: ToxFriendNumber,
      pcm: Array[Short],
      channels: AudioChannels,
      samplingRate: SamplingRate
    )(state0: State): State = {
      val state = state0.modify(_ + pcm.length)

      debug(state, s"Received audio frame: ${state.get} / ${audio.length(samplingRate)}")
      assert(channels == AudioChannels.Mono)
      assert(samplingRate == this.samplingRate)
      playback.play(pcm)

      if (state.get >= audio.length(samplingRate)) {
        playback.close()
        waitForPlayback(audio.length(samplingRate))(state)
      } else {
        state
      }
    }

  }

}
