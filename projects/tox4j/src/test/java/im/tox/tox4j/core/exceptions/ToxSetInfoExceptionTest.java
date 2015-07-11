package im.tox.tox4j.core.exceptions;

import im.tox.tox4j.ToxCoreTestBase;
import im.tox.tox4j.ToxCoreTestBase$;
import im.tox.tox4j.core.ToxCore;
import im.tox.tox4j.core.ToxCoreConstants;
import org.junit.Test;
import scala.runtime.BoxedUnit;

import static org.junit.Assert.assertEquals;

public class ToxSetInfoExceptionTest extends ToxCoreTestBase {

  @Test
  public void testSetNameTooLong() throws Exception {
    try (ToxCore<BoxedUnit> tox = newTox()) {
      byte[] array = ToxCoreTestBase$.MODULE$.randomBytes(ToxCoreConstants.MAX_NAME_LENGTH() + 1);
      tox.setName(array);
      fail();
    } catch (ToxSetInfoException e) {
      assertEquals(ToxSetInfoException.TOO_LONG$.MODULE$, e.code());
    }
  }

  @Test
  public void testSetStatusMessageTooLong() throws Exception {
    try (ToxCore<BoxedUnit> tox = newTox()) {
      byte[] array = ToxCoreTestBase$.MODULE$.randomBytes(ToxCoreConstants.MAX_STATUS_MESSAGE_LENGTH() + 1);
      tox.setStatusMessage(array);
      fail();
    } catch (ToxSetInfoException e) {
      assertEquals(ToxSetInfoException.TOO_LONG$.MODULE$, e.code());
    }
  }

  @Test
  public void testSetStatusMessageNull() throws Exception {
    try (ToxCore<BoxedUnit> tox = newTox()) {
      tox.setStatusMessage(null);
      fail();
    } catch (ToxSetInfoException e) {
      assertEquals(ToxSetInfoException.NULL$.MODULE$, e.code());
    }
  }

  @Test
  public void testSetNameNull() throws Exception {
    try (ToxCore<BoxedUnit> tox = newTox()) {
      tox.setName(null);
      fail();
    } catch (ToxSetInfoException e) {
      assertEquals(ToxSetInfoException.NULL$.MODULE$, e.code());
    }
  }

}
