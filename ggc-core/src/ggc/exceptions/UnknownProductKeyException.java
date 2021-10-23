package ggc.exceptions;

/** Exception for unknown product keys. */
public class UnknownProductKeyException extends Exception {

  /** Serial number for serialization. */
  private static final long serialVersionUID = 202110231557L;

  /** The key that had an attempted access */
  private String key;

  /** @param key Unknown key provided. */
  public UnknownProductKeyException(String key) {
    super();
    this.key = key;
  }

  /**
   * @param key   Unknown key provided.
   * @param cause What exception caused this one.
   */
  public UnknownProductKeyException(String key, Exception cause) {
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