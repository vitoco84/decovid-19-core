package ch.vitoco.decovid19core.exception;

public class ImageCorruptedException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public ImageCorruptedException(String message) {
    super(message);
  }

  public ImageCorruptedException(String message, Throwable cause) {
    super(message, cause);
  }

}
