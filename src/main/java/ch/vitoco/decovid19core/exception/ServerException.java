package ch.vitoco.decovid19core.exception;

/**
 * Representation class ServerException.
 */
public class ServerException extends RuntimeException {

  /**
   * Constructor.
   *
   * @param message the message
   * @param cause   the cause
   */
  public ServerException(String message, Throwable cause) {
    super(message, cause);
  }

}
