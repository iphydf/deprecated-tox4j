package im.tox.tox4j.apidsl

import scala.annotation.StaticAnnotation
import scala.language.experimental.macros
import scala.reflect.macros.whitebox

object IgnoredWarts {
  final val Any = "org.brianmckenna.wartremover.warts.Any"
  final val AsInstanceOf = "org.brianmckenna.wartremover.warts.AsInstanceOf"
}

@SuppressWarnings(Array(IgnoredWarts.Any, IgnoredWarts.AsInstanceOf))
object NativeApi {

  def impl(c: whitebox.Context)(annottees: c.Expr[Any]*): c.Expr[Any] = {
    import c.universe._
    val result = {
      annottees.map(_.tree).toList match {
        case List(definition @ q"..$mods trait $name[..$tparams] extends ..$parents { ..$body }") =>
          c.info(definition.pos, "YEP", force = false)
        case _ =>
      }
    }
    annottees.head
  }

}

@SuppressWarnings(Array(IgnoredWarts.Any))
final class NativeApi extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro NativeApi.impl
}
