package ch.vitoco.decovid19core.exception;

public class MessageDecodeException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public MessageDecodeException(String message) {
    super(message);
  }

  public MessageDecodeException(String message, Throwable cause) {
    super(message, cause);
  }

}
