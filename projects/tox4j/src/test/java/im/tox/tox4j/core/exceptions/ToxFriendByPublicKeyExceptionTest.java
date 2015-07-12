package im.tox.tox4j.core.exceptions;

import im.tox.tox4j.ToxCoreTestBase;
import im.tox.tox4j.core.ToxCore;
import org.junit.Test;
import scala.runtime.BoxedUnit;

import static org.junit.Assert.assertEquals;

public class ToxFriendByPublicKeyExceptionTest extends ToxCoreTestBase {

  @Test
  public void testNull() throws Exception {
    try (ToxCore<BoxedUnit> tox = newTox()) {
      tox.getFriendByPublicKey(null);
      fail();
    } catch (ToxFriendByPublicKeyException e) {
      assertEquals(ToxFriendByPublicKeyException.NULL$.MODULE$, e.code());
    }
  }

  @Test
  public void testNotFound() throws Exception {
    try (ToxCore<BoxedUnit> tox = newTox()) {
      tox.getFriendByPublicKey(tox.getPublicKey());
      fail();
    } catch (ToxFriendByPublicKeyException e) {
      assertEquals(ToxFriendByPublicKeyException.NOT_FOUND$.MODULE$, e.code());
    }
  }

}
