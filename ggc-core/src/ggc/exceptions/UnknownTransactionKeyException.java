package ggc.exceptions;

import java.io.Serial;

public class UnknownTransactionKeyException extends Exception {
  /**
   * Serial number for serialization.
   */
  @Serial
  private static final long serialVersionUID = 202111061536L;

  /**
   * The key that had an attempted access
   */
  private int key;

  /**
   * @param key Unknown key provided.
   */
  public UnknownTransactionKeyException(int key) {
    super();
    this.key = key;
  }

  /**
   * @param key   Unknown key provided.
   * @param cause What exception caused this one.
   */
  public UnknownTransactionKeyException(int key, Exception cause) {
    super(cause);
    this.key = key;
  }

  /**
   * @return the key
   */
  public int getKey() {
    return this.key;
  }
}
