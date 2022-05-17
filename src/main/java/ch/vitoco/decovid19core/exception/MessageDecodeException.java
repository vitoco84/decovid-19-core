package ch.vitoco.decovid19core.exception;

/**
 * Representation class MessageDecodeException.
 */
public class MessageDecodeException extends RuntimeException {

  /**
   * Constructor.
   *
   * @param message the message
   * @param cause   the cause
   */
  public MessageDecodeException(String message, Throwable cause) {
    super(message, cause);
  }

}
