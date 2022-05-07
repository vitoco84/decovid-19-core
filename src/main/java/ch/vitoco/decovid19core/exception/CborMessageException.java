package ch.vitoco.decovid19core.exception;

public class CborMessageException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public CborMessageException(String message) {
    super(message);
  }

  public CborMessageException(String message, Throwable cause) {
    super(message, cause);
  }

}
