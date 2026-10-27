package com.example.hospitalManagement;

import com.example.hospitalManagement.entities.Insurance;
import com.example.hospitalManagement.entities.Patient;
import com.example.hospitalManagement.services.InsuranceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

@SpringBootTest
public class InsuranceTests {

    @Autowired
    private InsuranceService insuranceService;

    @Test
    public void testInsurance(){
        Insurance insurance = Insurance.builder()
                .policyNumber("CIVIC_1234")
                .provider("ICIC")
                .validUntil(LocalDate.of(2030, 12, 1))
                .build();


        Patient patient = insuranceService.assignInsuranceToPatient(insurance, 1);

        System.out.println(patient);
    };

    // orphanRemoval and Removal of Cascade
    @Test
    public void testRemovalOfInsurance(){
        Insurance insurance = Insurance.builder()
                .policyNumber("SBI_1234")
                .provider("SBI")
                .validUntil(LocalDate.of(2030, 12, 1))
                .build();

        Patient patient = insuranceService.assignInsuranceToPatient(insurance, 2);

        System.out.println(patient);

        var newPatient = insuranceService.disassociateInsuranceFromPatient(patient.getId());

        System.out.println(newPatient);
    };

}
