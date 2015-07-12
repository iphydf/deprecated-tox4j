package im.tox.tox4j.core.exceptions;

import im.tox.tox4j.ToxCoreTestBase;
import im.tox.tox4j.core.ToxCore;
import im.tox.tox4j.core.ToxCoreConstants;
import org.junit.Before;
import org.junit.Test;
import scala.runtime.BoxedUnit;

import static org.junit.Assert.assertEquals;

public class ToxFriendAddExceptionTest extends ToxCoreTestBase {

  private byte[] validAddress;

  @Before
  public void setUp() throws Exception {
    validAddress = newTox().getAddress();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidAddress1() throws Exception {
    try (ToxCore tox = newTox()) {
      tox.addFriend(new byte[1], new byte[1]);
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidAddress2() throws Exception {
    try (ToxCore<BoxedUnit> tox = newTox()) {
      tox.addFriend(new byte[ToxCoreConstants.TOX_ADDRESS_SIZE() - 1], new byte[1]);
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidAddress3() throws Exception {
    try (ToxCore<BoxedUnit> tox = newTox()) {
      tox.addFriend(new byte[ToxCoreConstants.TOX_ADDRESS_SIZE() + 1], new byte[1]);
    }
  }

  @Test
  public void testNull1() throws Exception {
    try (ToxCore<BoxedUnit> tox = newTox()) {
      tox.addFriend(null, new byte[1]);
      fail();
    } catch (ToxFriendAddException e) {
      assertEquals(ToxFriendAddException.NULL$.MODULE$, e.code());
    }
  }

  @Test
  public void testNull2() throws Exception {
    try (ToxCore<BoxedUnit> tox = newTox()) {
      tox.addFriend(validAddress, null);
      fail();
    } catch (ToxFriendAddException e) {
      assertEquals(ToxFriendAddException.NULL$.MODULE$, e.code());
    }
  }

  @Test
  public void testNot_TooLong1() throws Exception {
    try (ToxCore<BoxedUnit> tox = newTox()) {
      tox.addFriend(validAddress, new byte[ToxCoreConstants.MAX_FRIEND_REQUEST_LENGTH() - 1]);
    }
  }

  @Test
  public void testNot_TooLong2() throws Exception {
    try (ToxCore<BoxedUnit> tox = newTox()) {
      tox.addFriend(validAddress, new byte[ToxCoreConstants.MAX_FRIEND_REQUEST_LENGTH()]);
    }
  }

  @Test
  public void testTooLong() throws Exception {
    try (ToxCore<BoxedUnit> tox = newTox()) {
      tox.addFriend(validAddress, new byte[ToxCoreConstants.MAX_FRIEND_REQUEST_LENGTH() + 1]);
      fail();
    } catch (ToxFriendAddException e) {
      assertEquals(ToxFriendAddException.TOO_LONG$.MODULE$, e.code());
    }
  }

  @Test
  public void testNoMessage() throws Exception {
    try (ToxCore<BoxedUnit> tox = newTox()) {
      tox.addFriend(validAddress, new byte[0]);
      fail();
    } catch (ToxFriendAddException e) {
      assertEquals(ToxFriendAddException.NO_MESSAGE$.MODULE$, e.code());
    }
  }

  @Test
  public void testOwnKey() throws Exception {
    try (ToxCore<BoxedUnit> tox = newTox()) {
      tox.addFriend(tox.getAddress(), new byte[1]);
      fail();
    } catch (ToxFriendAddException e) {
      assertEquals(ToxFriendAddException.OWN_KEY$.MODULE$, e.code());
    }
  }

  @Test
  public void testAlreadySent() throws Exception {
    try (ToxCore<BoxedUnit> tox = newTox()) {
      tox.addFriend(validAddress, new byte[1]);
      tox.addFriend(validAddress, new byte[1]);
      fail();
    } catch (ToxFriendAddException e) {
      assertEquals(ToxFriendAddException.ALREADY_SENT$.MODULE$, e.code());
    }
  }

  @Test
  public void testBadChecksum() throws Exception {
    try (ToxCore<BoxedUnit> tox = newTox()) {
      validAddress[0]++;
      tox.addFriend(validAddress, new byte[1]);
      fail();
    } catch (ToxFriendAddException e) {
      assertEquals(ToxFriendAddException.BAD_CHECKSUM$.MODULE$, e.code());
    }
  }

  @Test
  public void testSetNewNospam() throws Exception {
    try (ToxCore<BoxedUnit> tox = newTox()) {
      ToxCore friend = newTox();
      friend.setNoSpam(12345678);
      tox.addFriend(friend.getAddress(), new byte[1]);
      friend.setNoSpam(87654321);
      tox.addFriend(friend.getAddress(), new byte[1]);
      fail();
    } catch (ToxFriendAddException e) {
      assertEquals(ToxFriendAddException.SET_NEW_NOSPAM$.MODULE$, e.code());
    }
  }

  @Test
  public void testMalloc() throws Exception {
    // XXX: Can't test this.
    new ToxFriendAddException(ToxFriendAddException.MALLOC$.MODULE$, "").code();
  }

}
