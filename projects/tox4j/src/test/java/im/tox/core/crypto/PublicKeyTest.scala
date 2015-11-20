package im.tox.core.crypto

import im.tox.core.ModuleCompanionTest
import im.tox.core.random.RandomCore
import org.scalacheck.{Arbitrary, Gen}

object PublicKeyTest {

  def take(publicKey: PublicKey, maxSize: Int): PublicKey = {
    new PublicKey(publicKey.value.take(maxSize))
  }

  implicit val arbPublicKey: Arbitrary[PublicKey] =
    Arbitrary(Gen.resultOf[Unit, PublicKey] { case () => new PublicKey(RandomCore.randomBytes(PublicKey.Size)) })

}

final class PublicKeyTest extends ModuleCompanionTest(PublicKey) {

  override val arbT = PublicKeyTest.arbPublicKey

}
