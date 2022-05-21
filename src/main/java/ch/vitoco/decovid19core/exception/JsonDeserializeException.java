package ch.vitoco.decovid19core.exception;

/**
 * Representation class JsonDeserializeException.
 */
public class JsonDeserializeException extends RuntimeException {

  /**
   * Constructor.
   *
   * @param message the message
   * @param cause   the cause
   */
  public JsonDeserializeException(String message, Throwable cause) {
    super(message, cause);
  }

}
