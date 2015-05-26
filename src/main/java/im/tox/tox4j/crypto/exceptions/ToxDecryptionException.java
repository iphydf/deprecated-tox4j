package im.tox.tox4j.crypto.exceptions;

import im.tox.tox4j.annotations.NotNull;
import im.tox.tox4j.exceptions.ToxException;

public final class ToxDecryptionException extends ToxException<ToxDecryptionException.Code> {

  public enum Code {
    /**
     * The input data is missing the magic number (i.e. wasn't created by this
     * module, or is corrupted)
     */
    BAD_FORMAT,
    /**
     * The encrypted byte array could not be decrypted. Either the data was
     * corrupt or the password/key was incorrect.
     */
    FAILED,
    /**
     * The input data was shorter than {@link ToxConstants.PASS_ENCRYPTION_EXTRA_LENGTH} bytes.
     */
    INVALID_LENGTH,
    /**
     * The crypto lib was unable to derive a key from the given passphrase,
     * which is usually a lack of memory issue. The functions accepting keys
     * do not produce this error.
     */
    KEY_DERIVATION_FAILED,
    /**
     * Some input data, or maybe the output pointer, was null.
     */
    NULL,
  }

  public ToxDecryptionException(@NotNull Code code) {
    this(code, "");
  }

  public ToxDecryptionException(@NotNull Code code, String message) {
    super(code, message);
  }

}
