package ggc.exceptions;

import java.io.Serial;

/**
 * Exception for unknown product keys.
 */
public class UnknownPartnerKeyException extends Exception {

  /** Serial number for serialization. */
  @Serial
  private static final long serialVersionUID = 202110232154L;

  /** The key that had an attempted access */
  private String key;

  /** @param key Unknown key provided. */
  public UnknownPartnerKeyException(String key) {
    super();
    this.key = key;
  }

  /**
   * @param key   Unknown key provided.
   * @param cause What exception caused this one.
   */
  public UnknownPartnerKeyException(String key, Exception cause) {
    super(cause);
    this.key = key;
  }

  /**
   * @return the key
   */
  public String getKey() {
    return this.key;
  }
}