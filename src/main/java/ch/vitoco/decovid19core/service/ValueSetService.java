package ch.vitoco.decovid19core.service;

import java.util.List;
import java.util.Map;

import ch.vitoco.decovid19core.model.hcert.Hcert;
import ch.vitoco.decovid19core.model.hcert.HcertRecovery;
import ch.vitoco.decovid19core.model.hcert.HcertTest;
import ch.vitoco.decovid19core.model.hcert.HcertVaccination;
import ch.vitoco.decovid19core.utils.HcertValueSet;
import ch.vitoco.decovid19core.model.valueset.ValueSetValues;
import org.springframework.stereotype.Service;

/**
 * Service class for the Health Certificate ValueSet mappings.
 */
@Service
public class ValueSetService {

  /**
   * Maps the value set codes of the Vaccination Health Certificate information to the display names.
   *
   * @param hcertVaccinationList List of Health Certificate Vaccination information
   */
  public void mappingVaccinationValueSet(List<HcertVaccination> hcertVaccinationList) {
    Map<String, ValueSetValues> vaccineMarketingAuthValueMap = HcertValueSet.getVaccineMarketingAuthorisations()
        .getValueSetValues();
    Map<String, ValueSetValues> vaccineMedicinalProdValueMap = HcertValueSet.getVaccineMedicinalProduct()
        .getValueSetValues();
    Map<String, ValueSetValues> vaccineProphylaxisValueMap = HcertValueSet.getVaccineProphylaxis().getValueSetValues();

    if (hcertVaccinationList != null) {
      for (HcertVaccination hcertVacc : hcertVaccinationList) {
        if (vaccineMarketingAuthValueMap.containsKey(hcertVacc.getManufacturer())) {
          hcertVacc.setManufacturer(vaccineMarketingAuthValueMap.get(hcertVacc.getManufacturer()).getDisplay());
        }
        if (vaccineMedicinalProdValueMap.containsKey(hcertVacc.getVaccineProduct())) {
          hcertVacc.setVaccineProduct(vaccineMedicinalProdValueMap.get(hcertVacc.getVaccineProduct()).getDisplay());
        }
        if (vaccineProphylaxisValueMap.containsKey(hcertVacc.getVaccineProphylaxis())) {
          hcertVacc.setVaccineProphylaxis(vaccineProphylaxisValueMap.get(hcertVacc.getVaccineProphylaxis()).getDisplay());
        }
      }
    }
    mappingMutualValueSet(hcertVaccinationList);
  }

  /**
   * Maps the value set codes of the Test Health Certificate information to the display names.
   *
   * @param hcertTestList List of Health Certificate Test information
   */
  public void mappingTestValueSet(List<HcertTest> hcertTestList) {
    Map<String, ValueSetValues> testDeviceValueMap = HcertValueSet.getTestDevice().getValueSetValues();
    Map<String, ValueSetValues> testTypeValueMap = HcertValueSet.getTestType().getValueSetValues();
    Map<String, ValueSetValues> testResultValueMap = HcertValueSet.getTestResult().getValueSetValues();

    if (hcertTestList != null) {
      for (HcertTest hcertTest : hcertTestList) {
        if (testDeviceValueMap.containsKey(hcertTest.getManufacturer())) {
          hcertTest.setManufacturer(testDeviceValueMap.get(hcertTest.getManufacturer()).getDisplay());
        }
        if (testTypeValueMap.containsKey(hcertTest.getTypeOfTest())) {
          hcertTest.setTypeOfTest(testTypeValueMap.get(hcertTest.getTypeOfTest()).getDisplay());
        }
        if (testResultValueMap.containsKey(hcertTest.getTestResult())) {
          hcertTest.setTestResult(testResultValueMap.get(hcertTest.getTestResult()).getDisplay());
        }
      }
    }
    mappingMutualValueSet(hcertTestList);
  }

  /**
   * Maps the value set codes of the Recovery Health Certificate information to the display names.
   *
   * @param hcertRecoveryList List of Health Certificate Recovery information
   */
  public void mappingRecoveryValueSet(List<HcertRecovery> hcertRecoveryList) {
    mappingMutualValueSet(hcertRecoveryList);
  }

  private <T extends Hcert> void mappingMutualValueSet(List<T> list) {
    Map<String, ValueSetValues> countryCodesValueMap = HcertValueSet.getCountryCodes().getValueSetValues();
    Map<String, ValueSetValues> diseaseAgentValueMap = HcertValueSet.getDiseaseAgentTarget().getValueSetValues();

    if (list != null) {
      for (T entry : list) {
        if (countryCodesValueMap.containsKey(entry.getCountry())) {
          entry.setCountry(countryCodesValueMap.get(entry.getCountry()).getDisplay());
        }
        if (diseaseAgentValueMap.containsKey(entry.getTarget())) {
          entry.setTarget(diseaseAgentValueMap.get(entry.getTarget()).getDisplay());
        }
      }
    }
  }

}
