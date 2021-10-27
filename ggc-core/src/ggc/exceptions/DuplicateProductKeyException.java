package ggc.exceptions;

public class DuplicateProductKeyException extends Exception {

  private final String key;

  public DuplicateProductKeyException(String key) {
    this.key = key;
  }

  public String getKey() {
    return key;
  }
}
