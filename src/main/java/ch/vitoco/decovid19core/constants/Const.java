package ch.vitoco.decovid19core.constants;

public final class Const {

  public static final String IMAGE_DECODE_EXCEPTION = "The health certificate content of the image could not be decoded.";
  public static final String IMAGE_CORRUPTED_EXCEPTION = "The uploaded image is corrupted.";
  public static final String COSE_FORMAT_EXCEPTION = "The COSE message could not be formatted correctly.";
  public static final String MESSAGE_DECODE_EXCEPTION = "The message could not be decoded.";
  public static final String JSON_DESERIALIZE_EXCEPTION = "The payload could not be deserialized.";
  public static final String UTILITY_CLASS_EXCEPTION = "Utility class.";
  public static final String RESOURCES_READ_EXCEPTION = "Could not read resources.";

  private Const() {
    throw new IllegalStateException(UTILITY_CLASS_EXCEPTION);
  }

}
