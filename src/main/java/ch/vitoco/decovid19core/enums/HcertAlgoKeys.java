package ch.vitoco.decovid19core.enums;

/**
 * Representation enum of supported signature algorithms.
 */
public enum HcertAlgoKeys {
  /**
   * ECDSA with SHA-256.
   */
  ES256(-7, "ES256", "SHA256withECDSA"),
  /**
   * ECDSA with SHA-384.
   */
  ES384(-35, "ES384", "SHA384withECDSA"),
  /**
   * ECDSA with SHA-512.
   */
  ES512(-36, "ES512", "SHA512withECDSA"),
  /**
   * RSASSA-PSS with SHA-256.
   */
  PS256(-37, "PS256", "SHA256withRSA/PSS"),
  /**
   * RSASSA-PSS with SHA-384.
   */
  PS384(-38, "PS384", "SHA384withRSA/PSS"),
  /**
   * RSASSA-PSS with SHA-512.
   */
  PS512(-39, "PS512", "SHA512withRSA/PSS");

  /**
   * The algorithm id.
   */
  private final int algoId;
  /**
   * The algorithm name.
   */
  private final String name;
  /**
   * The JCA algorithm name.
   */
  private final String jcaAlgoName;

  /**
   * Constructor.
   *
   * @param algoId      the value of the algorithm id as int
   * @param name        the name of the algorithm
   * @param jcaAlgoName the Java Cryptography Architecture (JCA) algorithm name
   */
  HcertAlgoKeys(int algoId, String name, String jcaAlgoName) {
    this.algoId = algoId;
    this.name = name;
    this.jcaAlgoName = jcaAlgoName;
  }

  /**
   * Gets the algorithm id as int.
   *
   * @return algorithm id as int
   */
  public int getAlgoId() {
    return this.algoId;
  }

  /**
   * Gets the algorithm name.
   *
   * @return algorithm name
   */
  public String getName() {
    return name;
  }

  /**
   * Gets the JCA algorithm name.
   *
   * @return the JCA algorithm name
   */
  public String getJcaAlgoName() {
    return jcaAlgoName;
  }
}
