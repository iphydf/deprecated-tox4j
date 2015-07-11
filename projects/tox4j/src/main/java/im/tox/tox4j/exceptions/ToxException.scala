package im.tox.tox4j.exceptions

import org.jetbrains.annotations.NotNull

object ToxException {
  trait Code
}

abstract class ToxException[Code](message: String) extends Exception(message) {

  def code: Code

  @NotNull
  final override def getMessage: String = {
    message match {
      case "" => "Error code: " + code
      case _  => message + ", error code: " + code
    }
  }

}
