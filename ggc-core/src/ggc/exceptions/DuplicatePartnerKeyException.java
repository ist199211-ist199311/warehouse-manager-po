package ggc.exceptions;

import java.io.Serial;

public class DuplicatePartnerKeyException extends Exception {

  /**
   * Class serial number.
   */
  @Serial
  private static final long serialVersionUID = 202110291053L;

  private final String key;

  public DuplicatePartnerKeyException(String key) {
    this.key = key;
  }

  public String getKey() {
    return key;
  }
}
