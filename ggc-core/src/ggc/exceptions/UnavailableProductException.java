package ggc.exceptions;

import java.io.Serial;

/**
 * Exception thrown when a product is unavailable.
 */
public class UnavailableProductException extends Exception {

  /** Serial number for serialization. */
  @Serial
  private static final long serialVersionUID = 202111071611L;

  private String key;
  private int requested;
  private int available;

  /**
   * @param key       the requested key
   * @param requested Requested amount.
   * @param available Available amount.
   */
  public UnavailableProductException(String key, int requested, int available) {
    this.key = key;
    this.requested = requested;
    this.available = available;
  }

  public String getKey() {
    return this.key;
  }

  public int getRequested() {
    return this.requested;
  }

  public int getAvailable() {
    return this.available;
  }

}
