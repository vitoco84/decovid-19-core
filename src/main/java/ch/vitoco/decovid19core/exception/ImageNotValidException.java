package ch.vitoco.decovid19core.exception;

public class ImageNotValidException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public ImageNotValidException(String message, Throwable cause) {
    super(message, cause);
  }

}
