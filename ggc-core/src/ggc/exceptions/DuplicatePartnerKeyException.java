package ggc.exceptions;

public class DuplicatePartnerKeyException extends Exception {

  private final String key;

  public DuplicatePartnerKeyException(String key) {
    this.key = key;
  }

  public String getKey() {
    return key;
  }
}
