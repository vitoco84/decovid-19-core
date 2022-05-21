package ch.vitoco.decovid19core.constants;

/**
 * Representation class of messages for exceptions
 */
public final class Const {

  /**
   * QR-Code decode exception message.
   */
  public static final String QR_CODE_DECODE_EXCEPTION = "The Health Certificate content of the QR-Code could not be decoded.";
  /**
   * QR-Code corrupted exception message.
   */
  public static final String QR_CODE_CORRUPTED_EXCEPTION = "The uploaded QR-Code is corrupted.";
  /**
   * COSE format exception message.
   */
  public static final String MESSAGE_FORMAT_EXCEPTION = "The Message could not be formatted correctly.";
  /**
   * Decode exception message.
   */
  public static final String MESSAGE_DECODE_EXCEPTION = "The Message could not be decoded.";
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
