package im.tox.tox4j.core

import im.tox.tox4j.ConnectedListener
import im.tox.tox4j.ToxCoreImpl

final class RepeatedLanDiscoveryTest {
  @throws(classOf[Exception])
  def main(args: Array[String]) {
    for (i <- 1 to 1000) {
      println("Cycle " + i)
      var options = new ToxOptions()
      var tox1 = new ToxCoreImpl(options, null)
      var tox2 = new ToxCoreImpl(options, null)
      var status = new ConnectedListener()
      tox1.callbackConnectionStatus(status)
      tox2.callbackConnectionStatus(status)

      var start = System.currentTimeMillis()
      var last = start
      while (!status.isConnected) {
        tox1.iteration()
        tox2.iteration()
        Thread.sleep(Math.max(tox1.iterationInterval(), tox2.iterationInterval()))
        var now = System.currentTimeMillis()
        if ((now - last) > 10000) {
          println("Cycle is taking a long time: " + (now - start) + "ms")
          last = now
        }
      }
    }
  }
}
