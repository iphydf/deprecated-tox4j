package im.tox.annotations.constraint

import scala.annotation.StaticAnnotation

final case class LengthConstraint(value: Int) extends StaticAnnotation
