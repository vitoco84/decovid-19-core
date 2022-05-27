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
   * JSON serialize exception message.
   */
  public static final String JSON_SERIALIZE_EXCEPTION = "The payload could not be serialized.";
  /**
   * Utility class exception message.
   */
  public static final String UTILITY_CLASS_EXCEPTION = "Utility class.";
  /**
   * Resources read exception message.
   */
  public static final String RESOURCES_READ_EXCEPTION = "Could not read resources.";
  /**
   * URL encode exception message.
   */
  public static final String URL_ENCODE_EXCEPTION = "Could not encode the given URL.";
  /**
   * HCERT Test encode exception message.
   */
  public static final String HCERT_TEST_ENCODE_EXCEPTION = "Could not encode the given Test Certificate.";
  /**
   * Private or Public Key exception message.
   */
  public static final String KEY_SPEC_EXCEPTION = "The public or private key could not be retrieved.";
  /**
   * CBOR signature exception.
   */
  public static final String CBOR_SIGNATURE_EXCEPTION = "The CBOR Message could not be signed.";
  /**
   * COSE compress data exception.
   */
  public static final String COSE_COMPRESS_EXCEPTION = "The COSE Message could not be compressed.";

  /**
   * Constructor.
   */
  private Const() {
    throw new IllegalStateException(UTILITY_CLASS_EXCEPTION);
  }

}
