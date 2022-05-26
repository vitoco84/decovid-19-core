package ch.vitoco.decovid19core.service;

import java.util.List;
import java.util.Map;

import ch.vitoco.decovid19core.model.Hcert;
import ch.vitoco.decovid19core.model.HcertRecovery;
import ch.vitoco.decovid19core.model.HcertTest;
import ch.vitoco.decovid19core.model.HcertVaccination;
import ch.vitoco.decovid19core.valuesets.HcertValueSet;
import ch.vitoco.decovid19core.valuesets.model.ValueSetValues;
import org.springframework.stereotype.Service;

/**
 * Service class for the Health Certificate ValueSet mappings.
 */
@Service
public class Decovid19ValueSetService {

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
        if (vaccineMarketingAuthValueMap.containsKey(hcertVacc.getMa())) {
          hcertVacc.setMa(vaccineMarketingAuthValueMap.get(hcertVacc.getMa()).getDisplay());
        }
        if (vaccineMedicinalProdValueMap.containsKey(hcertVacc.getMp())) {
          hcertVacc.setMp(vaccineMedicinalProdValueMap.get(hcertVacc.getMp()).getDisplay());
        }
        if (vaccineProphylaxisValueMap.containsKey(hcertVacc.getVp())) {
          hcertVacc.setVp(vaccineProphylaxisValueMap.get(hcertVacc.getVp()).getDisplay());
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
        if (testDeviceValueMap.containsKey(hcertTest.getMa())) {
          hcertTest.setMa(testDeviceValueMap.get(hcertTest.getMa()).getDisplay());
        }
        if (testTypeValueMap.containsKey(hcertTest.getTt())) {
          hcertTest.setTt(testTypeValueMap.get(hcertTest.getTt()).getDisplay());
        }
        if (testResultValueMap.containsKey(hcertTest.getTr())) {
          hcertTest.setTr(testResultValueMap.get(hcertTest.getTr()).getDisplay());
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
        if (countryCodesValueMap.containsKey(entry.getCo())) {
          entry.setCo(countryCodesValueMap.get(entry.getCo()).getDisplay());
        }
        if (diseaseAgentValueMap.containsKey(entry.getTg())) {
          entry.setTg(diseaseAgentValueMap.get(entry.getTg()).getDisplay());
        }
      }
    }
  }

}
