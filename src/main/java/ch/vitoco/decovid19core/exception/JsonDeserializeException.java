package ch.vitoco.decovid19core.exception;

public class JsonDeserializeException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public JsonDeserializeException(String message) {
    super(message);
  }

  public JsonDeserializeException(String message, Throwable cause) {
    super(message, cause);
  }

}
