package ch.vitoco.decovid19core.constants;

/**
 * Representation class of messages for exceptions
 */
public final class Const {

  /**
   * Image decode exception message.
   */
  public static final String IMAGE_DECODE_EXCEPTION = "The health certificate content of the image could not be decoded.";
  /**
   * Image corrupted exception message.
   */
  public static final String IMAGE_CORRUPTED_EXCEPTION = "The uploaded image is corrupted.";
  /**
   * COSE format exception message.
   */
  public static final String COSE_FORMAT_EXCEPTION = "The COSE message could not be formatted correctly.";
  /**
   * Decode exception message.
   */
  public static final String MESSAGE_DECODE_EXCEPTION = "The message could not be decoded.";
  /**
   * JSON deserialize exception message.
   */
  public static final String JSON_DESERIALIZE_EXCEPTION = "The payload could not be deserialized.";
  /**
   * Utility class exception message.
   */
  public static final String UTILITY_CLASS_EXCEPTION = "Utility class.";
  /**
   * Resources read exception message.
   */
  public static final String RESOURCES_READ_EXCEPTION = "Could not read resources.";

  /**
   * Constructor.
   */
  private Const() {
    throw new IllegalStateException(UTILITY_CLASS_EXCEPTION);
  }

}
