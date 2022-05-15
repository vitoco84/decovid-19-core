package ch.vitoco.decovid19core.exception;

public class ResourcesNotFoundException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public ResourcesNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }

}
