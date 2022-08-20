package ch.vitoco.decovid19core.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;

import ch.vitoco.decovid19core.model.valueset.ValueSet;
import ch.vitoco.decovid19core.model.valueset.ValueSetValues;
import org.junit.jupiter.api.Test;

class HcertValueSetTest {

  @Test
  void shouldReturnCountryDisplayName() {
    ValueSet valueSet = HcertValueSet.getCountryCodes();
    Map<String, ValueSetValues> valueSetValues = valueSet.getValueSetValues();

    assertEquals("Switzerland", valueSetValues.get("CH").getDisplay());
    assertEquals("Germany", valueSetValues.get("DE").getDisplay());
    assertEquals("Italy", valueSetValues.get("IT").getDisplay());
  }

  @Test
  void shouldReturnDiseaseAgentTargetDisplayName() {
    ValueSet valueSet = HcertValueSet.getDiseaseAgentTarget();
    Map<String, ValueSetValues> valueSetValues = valueSet.getValueSetValues();

    assertEquals("COVID-19", valueSetValues.get("840539006").getDisplay());
  }

  @Test
  void shouldReturnVaccineMarketingAuthorisationDisplayName() {
    ValueSet valueSet = HcertValueSet.getVaccineMarketingAuthorisations();
    Map<String, ValueSetValues> valueSetValues = valueSet.getValueSetValues();

    assertEquals("AstraZeneca AB", valueSetValues.get("ORG-100001699").getDisplay());
    assertEquals("China Sinopharm International Corp. - Beijing location",
        valueSetValues.get("ORG-100020693").getDisplay());
  }

  @Test
  void shouldReturnVaccineMedicinalProductDisplayName() {
    ValueSet valueSet = HcertValueSet.getVaccineMedicinalProduct();
    Map<String, ValueSetValues> valueSetValues = valueSet.getValueSetValues();

    assertEquals("Spikevax", valueSetValues.get("EU/1/20/1507").getDisplay());
    assertEquals("EpiVacCorona", valueSetValues.get("EpiVacCorona").getDisplay());
    assertEquals("Vaxzevria", valueSetValues.get("EU/1/21/1529").getDisplay());
  }

  @Test
  void shouldReturnVaccineProphylaxisDisplayName() {
    ValueSet valueSet = HcertValueSet.getVaccineProphylaxis();
    Map<String, ValueSetValues> valueSetValues = valueSet.getValueSetValues();

    assertEquals("covid-19 vaccines", valueSetValues.get("J07BX03").getDisplay());
    assertEquals("COVID-19 non-replicating viral vector vaccine", valueSetValues.get("29061000087103").getDisplay());
    assertEquals("SARS-CoV-2 mRNA vaccine", valueSetValues.get("1119349007").getDisplay());
  }

  @Test
  void shouldReturnTestTypeDisplayName() {
    ValueSet valueSet = HcertValueSet.getTestType();
    Map<String, ValueSetValues> valueSetValues = valueSet.getValueSetValues();

    assertEquals("Nucleic acid amplification with probe detection", valueSetValues.get("LP6464-4").getDisplay());
    assertEquals("Rapid immunoassay", valueSetValues.get("LP217198-3").getDisplay());
  }

  @Test
  void shouldReturnTestDeviceDisplayName() {
    ValueSet valueSet = HcertValueSet.getTestDevice();
    Map<String, ValueSetValues> valueSetValues = valueSet.getValueSetValues();

    assertEquals("ACON Laboratories, Inc, Flowflex SARS-CoV-2 Antigen rapid test",
        valueSetValues.get("1468").getDisplay());
    assertEquals("LumiraDX, LumiraDx SARS-CoV-2 Ag Test", valueSetValues.get("1268").getDisplay());
    assertEquals("Shenzhen Zhenrui Biotechnology Co., Ltd, Zhenrui ®COVID-19 Antigen Test Cassette",
        valueSetValues.get("1574").getDisplay());
  }

  @Test
  void shouldReturnTestResultDisplayName() {
    ValueSet valueSet = HcertValueSet.getTestResult();
    Map<String, ValueSetValues> valueSetValues = valueSet.getValueSetValues();

    assertEquals("Not detected", valueSetValues.get("260415000").getDisplay());
    assertEquals("Detected", valueSetValues.get("260373001").getDisplay());
  }

}
