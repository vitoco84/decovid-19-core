package ch.vitoco.decovid19core.exception;

/**
 * Representation class ImageNotValidException.
 */
public class ImageNotValidException extends RuntimeException {

  /**
   * Constructor.
   *
   * @param message the message
   * @param cause   the cause
   */
  public ImageNotValidException(String message, Throwable cause) {
    super(message, cause);
  }

}
