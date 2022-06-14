package ch.vitoco.decovid19core.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import ch.vitoco.decovid19core.model.hcert.HcertContentDTO;
import ch.vitoco.decovid19core.model.hcert.HcertRecovery;
import ch.vitoco.decovid19core.model.hcert.HcertTest;
import ch.vitoco.decovid19core.model.hcert.HcertVaccination;

class ValueSetServiceTest {

  private final ValueSetService valueSetService = new ValueSetService();

  @Test
  void shouldMapVaccinationValueSet() {
    HcertVaccination hcertVaccination = buildHcertVaccination();
    List<HcertVaccination> hcertVaccinationList = List.of(hcertVaccination);

    HcertContentDTO hcertContentDTO = new HcertContentDTO();
    hcertContentDTO.setVaccination(hcertVaccinationList);

    valueSetService.mappingVaccinationValueSet(hcertContentDTO.getVaccination());
    HcertVaccination hcertVaccinationResult = hcertContentDTO.getVaccination().get(0);

    assertEquals("123456", hcertVaccinationResult.getCertIdentifier());
    assertEquals("Afghanistan", hcertVaccinationResult.getCountry());
    assertEquals(2L, hcertVaccinationResult.getNumberOfDoses());
    assertEquals("2022-06-06", hcertVaccinationResult.getVaccinationDate());
    assertEquals("issuer", hcertVaccinationResult.getIssuer());
    assertEquals("Biontech Manufacturing GmbH", hcertVaccinationResult.getManufacturer());
    assertEquals("Spikevax", hcertVaccinationResult.getVaccineProduct());
    assertEquals(4L, hcertVaccinationResult.getOverallNumberOfDoses());
    assertEquals("COVID-19", hcertVaccinationResult.getTarget());
    assertEquals("covid-19 vaccines", hcertVaccinationResult.getVaccineProphylaxis());
  }

  @Test
  void shouldMapTestValueSet() {
    HcertTest hcertTest = buildHcertTest();
    List<HcertTest> hcertTestList = List.of(hcertTest);

    HcertContentDTO hcertContentDTO = new HcertContentDTO();
    hcertContentDTO.setTest(hcertTestList);

    valueSetService.mappingTestValueSet(hcertContentDTO.getTest());
    HcertTest hcertTestResult = hcertContentDTO.getTest().get(0);

    assertEquals("COVID-19", hcertTestResult.getTarget());
    assertEquals("Nucleic acid amplification with probe detection", hcertTestResult.getTypeOfTest());
    assertEquals("nucleic-name", hcertTestResult.getNucleicAcidAmplName());
    assertEquals("Abbott Rapid Diagnostics, Panbio COVID-19 Ag Rapid Test",
        hcertTestResult.getTestDeviceManufacturer());
    assertEquals("2022-06-06", hcertTestResult.getSampleCollectionDate());
    assertEquals("Not detected", hcertTestResult.getTestResult());
    assertEquals("testing-centre", hcertTestResult.getTestingCentre());
    assertEquals("Afghanistan", hcertTestResult.getCountry());
    assertEquals("issuer", hcertTestResult.getIssuer());
    assertEquals("123456", hcertTestResult.getCertIdentifier());
  }

  @Test
  void shouldMapRecoveryValueSet() {
    HcertRecovery hcertRecovery = buildHcertRecovery();
    List<HcertRecovery> hcertRecoveryList = List.of(hcertRecovery);

    HcertContentDTO hcertContentDTO = new HcertContentDTO();
    hcertContentDTO.setRecovery(hcertRecoveryList);

    valueSetService.mappingRecoveryValueSet(hcertContentDTO.getRecovery());
    HcertRecovery hcertRecoveryResult = hcertContentDTO.getRecovery().get(0);

    assertEquals("COVID-19", hcertRecoveryResult.getTarget());
    assertEquals("2022-06-06", hcertRecoveryResult.getFirstPositiveDateResult());
    assertEquals("Afghanistan", hcertRecoveryResult.getCountry());
    assertEquals("2022-06-06", hcertRecoveryResult.getValidFrom());
    assertEquals("2022-06-06", hcertRecoveryResult.getValidTo());
    assertEquals("issuer", hcertRecoveryResult.getIssuer());
    assertEquals("123456", hcertRecoveryResult.getCertIdentifier());
  }

  private HcertRecovery buildHcertRecovery() {
    HcertRecovery hcertRecovery = new HcertRecovery();
    hcertRecovery.setTarget("840539006");
    hcertRecovery.setFirstPositiveDateResult("2022-06-06");
    hcertRecovery.setCountry("AF");
    hcertRecovery.setValidFrom("2022-06-06");
    hcertRecovery.setValidTo("2022-06-06");
    hcertRecovery.setIssuer("issuer");
    hcertRecovery.setCertIdentifier("123456");
    return hcertRecovery;
  }

  private HcertVaccination buildHcertVaccination() {
    HcertVaccination hcertVaccination = new HcertVaccination();
    hcertVaccination.setCertIdentifier("123456");
    hcertVaccination.setCountry("AF");
    hcertVaccination.setNumberOfDoses(2L);
    hcertVaccination.setVaccinationDate("2022-06-06");
    hcertVaccination.setIssuer("issuer");
    hcertVaccination.setManufacturer("ORG-100030215");
    hcertVaccination.setVaccineProduct("EU/1/20/1507");
    hcertVaccination.setOverallNumberOfDoses(4L);
    hcertVaccination.setTarget("840539006");
    hcertVaccination.setVaccineProphylaxis("J07BX03");
    return hcertVaccination;
  }

  private HcertTest buildHcertTest() {
    HcertTest hcertTest = new HcertTest();
    hcertTest.setTarget("840539006");
    hcertTest.setTypeOfTest("LP6464-4");
    hcertTest.setNucleicAcidAmplName("nucleic-name");
    hcertTest.setTestDeviceManufacturer("1232");
    hcertTest.setSampleCollectionDate("2022-06-06");
    hcertTest.setTestResult("260415000");
    hcertTest.setTestingCentre("testing-centre");
    hcertTest.setCountry("AF");
    hcertTest.setIssuer("issuer");
    hcertTest.setCertIdentifier("123456");
    return hcertTest;
  }

}
