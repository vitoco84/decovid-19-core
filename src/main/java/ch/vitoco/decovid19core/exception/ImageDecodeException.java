package ch.vitoco.decovid19core.exception;

/**
 * Representation class ImageDecodeException.
 */
public class ImageDecodeException extends RuntimeException {

  /**
   * Constructor.
   *
   * @param message the message
   * @param cause   the cause
   */
  public ImageDecodeException(String message, Throwable cause) {
    super(message, cause);
  }

}
