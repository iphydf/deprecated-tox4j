package im.tox.tox4j.impl.tcp

object NoSpam {
  val SIZE = Integer.SIZE
}

final case class NoSpam(data: Int) extends Serializable
