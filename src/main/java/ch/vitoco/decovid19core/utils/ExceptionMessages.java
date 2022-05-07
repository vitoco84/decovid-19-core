package ch.vitoco.decovid19core.utils;

public class ExceptionMessages {

  public static final String IMAGE_DECODE_EXCEPTION_MESSAGE = "The health certificate content of the image could not be decoded.";
  public static final String IMAGE_CORRUPTED_EXCEPTION_MESSAGE = "The uploaded image is corrupted.";
  public static final String COSE_FORMAT_EXCEPTION_MESSAGE = "The COSE message could not be formatted correctly.";
  public static final String COSE_DECODE_EXCEPTION_MESSAGE = "The COSE message could not be decoded.";
  public static final String CBOR_DECODE_EXCEPTION_MESSAGE = "The CBOR message could not be decoded.";
  public static final String UTILITY_CLASS_EXCEPTION_MESSAGE = "Utility class.";

  private ExceptionMessages() {
    throw new IllegalStateException(UTILITY_CLASS_EXCEPTION_MESSAGE);
  }

}
