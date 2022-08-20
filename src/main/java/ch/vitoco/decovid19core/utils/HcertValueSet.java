package ch.vitoco.decovid19core.utils;

import static ch.vitoco.decovid19core.constants.ExceptionMessages.RESOURCES_READ_EXCEPTION;

import java.io.IOException;
import java.io.InputStream;

import ch.vitoco.decovid19core.exception.ServerException;
import ch.vitoco.decovid19core.model.valueset.ValueSet;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.core.io.ClassPathResource;

/**
 * Representation class for value sets from: <a href="https://github.com/ehn-dcc-development/eu-dcc-valuesets">eu-dcc-valuesets</a>.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class HcertValueSet {

  /**
   * Path of the country codes resources.
   */
  private static final ClassPathResource COUNTRY_CODES_VALUE_SET_PATH;
  /**
   * Path of the disease or agent targeted resources.
   */
  private static final ClassPathResource DISEASE_AGENT_TARGET_VALUE_SET_PATH;
  /**
   * Path of the vaccine marketing authorisation holder or manufacturer resources.
   */
  private static final ClassPathResource VACC_MARKETING_AUTHORISATION;
  /**
   * path of the vaccine medicinal product resources.
   */
  private static final ClassPathResource VACC_MEDICINAL_PRODUCT;
  /**
   * Path of the vaccine or prophylaxis resources.
   */
  private static final ClassPathResource VACC_PROPHYLAXIS;
  /**
   * Path of the test type resources.
   */
  private static final ClassPathResource TEST_TYPE;
  /**
   * Path ot the test device identifier resources.
   */
  private static final ClassPathResource TEST_DEVICE;
  /**
   * Path of the test result resources.
   */
  private static final ClassPathResource TEST_RESULT;

  static {
    COUNTRY_CODES_VALUE_SET_PATH = new ClassPathResource("valuesets/country-2-codes.json");
    DISEASE_AGENT_TARGET_VALUE_SET_PATH = new ClassPathResource("valuesets/disease-agent-targeted.json");
    VACC_MARKETING_AUTHORISATION = new ClassPathResource("valuesets/vaccine-mah-manf.json");
    VACC_MEDICINAL_PRODUCT = new ClassPathResource("valuesets/vaccine-medicinal-product.json");
    VACC_PROPHYLAXIS = new ClassPathResource("valuesets/vaccine-prophylaxis.json");
    TEST_TYPE = new ClassPathResource("valuesets/test-type.json");
    TEST_DEVICE = new ClassPathResource("valuesets/test-manf.json");
    TEST_RESULT = new ClassPathResource("valuesets/test-result.json");
  }

  /**
   * Gets the value set of the country codes.
   *
   * @return ValueSet of country codes
   */
  public static ValueSet getCountryCodes() {
    return getValueSet(COUNTRY_CODES_VALUE_SET_PATH);
  }

  /**
   * Gets the value set of the disease or agent targeted.
   *
   * @return ValueSet of disease or agent targeted
   */
  public static ValueSet getDiseaseAgentTarget() {
    return getValueSet(DISEASE_AGENT_TARGET_VALUE_SET_PATH);
  }

  /**
   * Gets the value set of the vaccine marketing authorisation holder or manufacturer.
   *
   * @return ValueSet of the vaccine marketing authorisation holder or manufacturer
   */
  public static ValueSet getVaccineMarketingAuthorisations() {
    return getValueSet(VACC_MARKETING_AUTHORISATION);
  }

  /**
   * Gets the value set of the vaccine product.
   *
   * @return ValueSet of the vaccine product
   */
  public static ValueSet getVaccineMedicinalProduct() {
    return getValueSet(VACC_MEDICINAL_PRODUCT);
  }

  /**
   * Gets the value set of the vaccine or prophylaxis.
   *
   * @return ValueSet of the vaccine or prophylaxis
   */
  public static ValueSet getVaccineProphylaxis() {
    return getValueSet(VACC_PROPHYLAXIS);
  }

  /**
   * Gets the value set of the test type.
   *
   * @return ValueSet of the test type
   */
  public static ValueSet getTestType() {
    return getValueSet(TEST_TYPE);
  }

  /**
   * Gets the value set of the test device identifier.
   *
   * @return ValueSet of the test device identifier
   */
  public static ValueSet getTestDevice() {
    return getValueSet(TEST_DEVICE);
  }

  /**
   * Gets the value set of the test result.
   *
   * @return ValueSet of the test result
   */
  public static ValueSet getTestResult() {
    return getValueSet(TEST_RESULT);
  }

  /**
   * Helper method for retrieving value sets from resources.
   *
   * @param classPathResource the ClassPathResource to the value sets resources
   * @return ValueSet of the resources
   */
  public static ValueSet getValueSet(ClassPathResource classPathResource) {
    try (InputStream inputStream = classPathResource.getInputStream()) {
      ObjectMapper objectMapper = new ObjectMapper();
      return objectMapper.readValue(inputStream, ValueSet.class);
    } catch (IOException e) {
      throw new ServerException(RESOURCES_READ_EXCEPTION, e);
    }
  }

}
