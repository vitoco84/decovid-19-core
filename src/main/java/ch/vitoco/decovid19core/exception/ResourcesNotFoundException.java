package ch.vitoco.decovid19core.exception;

/**
 * Representation class ResourcesNotFoundException.
 */
public class ResourcesNotFoundException extends RuntimeException {

  /**
   * Constructor.
   *
   * @param message the message
   * @param cause   the cause
   */
  public ResourcesNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }

}
