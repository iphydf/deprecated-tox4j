package im.tox.annotations.constraint

import scala.annotation.StaticAnnotation

final case class RangeConstraint(low: Int, high: Int) extends StaticAnnotation
