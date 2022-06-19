package ch.vitoco.decovid19core.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Representation class of messages for exceptions
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ExceptionMessages {

  /**
   * Barcode not found exception message.
   */
  public static final String BARCODE_NOT_FOUND_EXCEPTION = "The Barcode was not found in the provided image.";
  /**
   * QR-Code decode exception message.
   */
  public static final String QR_CODE_DECODE_EXCEPTION = "The Health Certificate content of the QR-Code could not be decoded.";
  /**
   * HCERT encode exception message.
   */
  public static final String QR_CODE_ENCODE_EXCEPTION = "The Health Certificate could not be encoded.";
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
   * Resources read exception message.
   */
  public static final String RESOURCES_READ_EXCEPTION = "Could not read resources.";
  /**
   * URL encode exception message.
   */
  public static final String URL_ENCODE_EXCEPTION = "Could not encode the given URL.";
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
   * Signature not valid exception.
   */
  public static final String INVALID_SIGNATURE = "The Signature or the provided keyId is not valid.";
  /**
   * Certificate retrieve exception.
   */
  public static final String CERTIFICATES_RETRIEVE_EXCEPTION = "The Health or Root Certificate could not be retrieved.";

}
