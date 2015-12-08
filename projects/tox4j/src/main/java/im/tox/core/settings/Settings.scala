package im.tox.core.settings

import im.tox.core.settings.Settings.{Setting, SettingKey}

import scala.reflect.ClassTag

@SuppressWarnings(Array("org.brianmckenna.wartremover.warts.Any"))
// scalastyle:off method.name
object Settings {

  private val registry = new scala.collection.mutable.HashSet[String]

  final case class SettingKey[A](key: String, default: A) {
    require(!registry.contains(key))
    registry += key

    def :=(value: A): Setting = {
      Setting(key, value)
    }
  }

  object SettingKey {

    final case class UnfinishedSettingKey(key: String) {
      def :=[A](value: A): SettingKey[A] = SettingKey(key, value)
    }

    def apply(key: String): UnfinishedSettingKey = UnfinishedSettingKey(key)

  }

  final case class Setting(key: String, value: Any)

}

@SuppressWarnings(Array(
  "org.brianmckenna.wartremover.warts.Any",
  "org.brianmckenna.wartremover.warts.AsInstanceOf",
  "org.brianmckenna.wartremover.warts.IsInstanceOf"
))
final case class Settings(settings: Setting*) {

  require {
    val keys = settings.map(_.key)
    keys == keys.distinct
  }

  def apply[A](key: SettingKey[A])(implicit classTag: ClassTag[A]): A = {
    settings.find(_.key == key.key) match {
      case Some(Setting(_, value: A)) => value
      case _                          => key.default
    }
  }

}
