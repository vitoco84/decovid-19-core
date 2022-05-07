package ch.vitoco.decovid19core.exception;

public class CoseMessageException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public CoseMessageException(String message) {
    super(message);
  }

  public CoseMessageException(String message, Throwable cause) {
    super(message, cause);
  }

}
