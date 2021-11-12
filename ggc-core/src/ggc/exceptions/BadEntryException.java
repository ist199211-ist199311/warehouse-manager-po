package ggc.exceptions;

import java.io.Serial;

/**
 * Exception for unknown import file entries.
 */
public class BadEntryException extends Exception {

  /**
   * Class serial number.
   */
  @Serial
  private static final long serialVersionUID = 201409301048L;

  /**
   * Bad entry specification.
   */
  private String _entrySpecification;

  /**
   * @param entrySpecification
   */
  public BadEntryException(String entrySpecification) {
    _entrySpecification = entrySpecification;
  }

  /**
   * @param entrySpecification
   * @param cause
   */
  public BadEntryException(String entrySpecification, Exception cause) {
    super(cause);
    _entrySpecification = entrySpecification;
  }

  /**
   * @return the bad entry specification.
   */
  public String getEntrySpecification() {
    return _entrySpecification;
  }

}
