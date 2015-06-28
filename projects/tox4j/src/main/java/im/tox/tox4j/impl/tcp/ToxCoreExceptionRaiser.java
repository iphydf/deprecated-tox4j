package im.tox.tox4j.impl.tcp;

import im.tox.tox4j.exceptions.ToxKilledException;
import im.tox.tox4j.core.exceptions.*;

public final class ToxCoreExceptionRaiser {

  public static <T> T raise(ToxBootstrapException.Code code) throws ToxBootstrapException {
    throw new ToxBootstrapException(code);
  }

  public static <T> T raise(ToxGetPortException.Code code) throws ToxGetPortException {
    throw new ToxGetPortException(code);
  }

  public static <T> T raise(ToxSetInfoException.Code code) throws ToxSetInfoException {
    throw new ToxSetInfoException(code);
  }

  public static <T> T raise(ToxFriendCustomPacketException.Code code) throws ToxFriendCustomPacketException {
    throw new ToxFriendCustomPacketException(code);
  }

  public static <T> T raise(ToxFriendSendMessageException.Code code) throws ToxFriendSendMessageException {
    throw new ToxFriendSendMessageException(code);
  }

  public static <T> T raise(ToxFriendGetPublicKeyException.Code code) throws ToxFriendGetPublicKeyException {
    throw new ToxFriendGetPublicKeyException(code);
  }

  public static <T> T raise(ToxFriendByPublicKeyException.Code code) throws ToxFriendByPublicKeyException {
    throw new ToxFriendByPublicKeyException(code);
  }

  public static <T> T raise(ToxFriendDeleteException.Code code) throws ToxFriendDeleteException {
    throw new ToxFriendDeleteException(code);
  }

  public static <T> T raise(ToxFriendAddException.Code code) throws ToxFriendAddException {
    throw new ToxFriendAddException(code);
  }

  public static <T> T raiseToxKilledException(String message) throws ToxKilledException {
    throw new ToxKilledException(message);
  }

  public static <T> T raiseUnsupportedOperationException(String message) throws UnsupportedOperationException {
    throw new UnsupportedOperationException(message);
  }

  public static <T> T raiseIllegalArgumentException(String message) throws IllegalArgumentException {
    throw new IllegalArgumentException(message);
  }
}
