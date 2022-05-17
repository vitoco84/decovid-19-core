package ch.vitoco.decovid19core.enums;

/**
 * Representation enum of supported signature algorithms.
 */
public enum HcertAlgo {
  /**
   * ECDSA with SHA-256.
   */
  ECDSA_256(-7),
  /**
   * ECDSA with SHA-384.
   */
  ECDSA_384(-35),
  /**
   * ECDSA with SHA-512.
   */
  ECDSA_512(-36),
  /**
   * RSASSA-PSS with SHA-256.
   */
  RSA_PSS_256(-37),
  /**
   * RSASSA-PSS with SHA-384.
   */
  RSA_PSS_384(-38),
  /**
   * RSASSA-PSS with SHA-512.
   */
  RSA_PSS_512(-39);

  /**
   * The algorithm id.
   */
  private final int algoId;

  /**
   * Constructor.
   *
   * @param algoId the value of the algorithm id as int
   */
  HcertAlgo(int algoId) {
    this.algoId = algoId;
  }

  /**
   * Gets the algorithm id as int.
   *
   * @return algorithm id as int
   */
  public int getAlgoId() {
    return this.algoId;
  }

}
