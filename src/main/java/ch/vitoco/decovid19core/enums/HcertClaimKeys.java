package ch.vitoco.decovid19core.enums;

/**
 * Representation enum of supported CBOR Message claim keys.
 */
public enum HcertClaimKeys {

  /**
   * Health Certificate claim key.
   */
  HCERT_CLAIM_KEY(-260),
  /**
   * Health Certificate message tag claim key.
   */
  HCERT_MESSAGE_TAG(1),
  /**
   * Health Certificate issuer claim key.
   */
  HCERT_ISSUER_CLAIM_KEY(1),
  /**
   * Health Certificate expiration time claim key.
   */
  HCERT_EXPIRATION_CLAIM_KEY(4),
  /**
   * Health Certificate issued at time claim key.
   */
  HCERT_ISSUED_AT_CLAIM_KEY(6),
  /**
   * Health Certificate version claim key.
   */
  HCERT_VERSION_CLAIM_KEY(1);

  /**
   * The Health Certificate claim key.
   */
  private final int claimKey;

  /**
   * Constructor.
   *
   * @param claimKey the value of the Health Certificate claim key.
   */
  HcertClaimKeys(int claimKey) {
    this.claimKey = claimKey;
  }

  /**
   * Gets the Health Certificate claim key as int.
   *
   * @return Health Certificate claim key as int
   */
  public int getClaimKey() {
    return this.claimKey;
  }
}
