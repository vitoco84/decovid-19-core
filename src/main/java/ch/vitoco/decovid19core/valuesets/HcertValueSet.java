package ch.vitoco.decovid19core.valuesets;

import static ch.vitoco.decovid19core.constants.Const.RESOURCES_READ_EXCEPTION;
import static ch.vitoco.decovid19core.constants.Const.UTILITY_CLASS_EXCEPTION;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import ch.vitoco.decovid19core.exception.ResourcesNotFoundException;
import ch.vitoco.decovid19core.valuesets.model.ValueSet;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Represantation class for value sets from: <a href="https://github.com/ehn-dcc-development/eu-dcc-valuesets">eu-dcc-valuesets</a>
 */
public final class HcertValueSet {

  private static final Path COUNTRY_CODES_VALUE_SET_PATH = Paths.get("src/main/resources/country-2-codes.json");
  private static final Path DISEASE_AGENT_TARGET_VALUE_SET_PATH = Paths.get(
      "src/main/resources/disease-agent-targeted.json");

  private static final Path VACC_MARKETING_AUTHORISATION = Paths.get("src/main/resources/vaccine-mah-manf.json");
  private static final Path VACC_MEDICINAL_PRODUCT = Paths.get("src/main/resources/vaccine-medicinal-product.json");
  private static final Path VACC_PROPHYLAXIS = Paths.get("src/main/resources/vaccine-prophylaxis.json");

  private static final Path TEST_TYPE = Paths.get("src/main/resources/test-type.json");
  private static final Path TEST_DEVICE = Paths.get("src/main/resources/test-manf.json");
  private static final Path TEST_RESULT = Paths.get("src/main/resources/test-result.json");

  private static final ObjectMapper jsonMapper = new ObjectMapper();


  private HcertValueSet() {
    throw new IllegalStateException(UTILITY_CLASS_EXCEPTION);
  }

  public static ValueSet getCountryCodes() {
    return getValueSet(COUNTRY_CODES_VALUE_SET_PATH);
  }

  public static ValueSet getDiseaseAgentTarget() {
    return getValueSet(DISEASE_AGENT_TARGET_VALUE_SET_PATH);
  }

  public static ValueSet getVaccineMarketingAuthorisations() {
    return getValueSet(VACC_MARKETING_AUTHORISATION);
  }

  public static ValueSet getVaccineMedicinalProduct() {
    return getValueSet(VACC_MEDICINAL_PRODUCT);
  }

  public static ValueSet getVaccineProphylaxis() {
    return getValueSet(VACC_PROPHYLAXIS);
  }

  public static ValueSet getTestType() {
    return getValueSet(TEST_TYPE);
  }

  public static ValueSet getTestDevice() {
    return getValueSet(TEST_DEVICE);
  }

  public static ValueSet getTestResult() {
    return getValueSet(TEST_RESULT);
  }

  private static InputStream getInputStream(Path path) throws IOException {
    return Files.newInputStream(path);
  }

  public static ValueSet getValueSet(Path countryCodesValueSetPath) {
    try (InputStream inputStream = getInputStream(countryCodesValueSetPath)) {
      return jsonMapper.readValue(inputStream, ValueSet.class);
    } catch (IOException e) {
      throw new ResourcesNotFoundException(RESOURCES_READ_EXCEPTION, e);
    }
  }

}
