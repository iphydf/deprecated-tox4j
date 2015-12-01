package im.tox.tox4j.exceptions

import org.jetbrains.annotations.NotNull

abstract class ToxException[E <: Enum[E]](
    final val code: E,
    message: String
) extends Exception(message) {

  @NotNull
  final override def getMessage: String = {
    message match {
      case "" => "Error code: " + code.name
      case _  => message + ", error code: " + code.name
    }
  }

}
