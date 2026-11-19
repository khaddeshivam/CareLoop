package com.example.hospitalManagement.services;

import com.example.hospitalManagement.entities.Insurance;
import com.example.hospitalManagement.entities.Patient;
import com.example.hospitalManagement.repository.InsuranceRepository;
import com.example.hospitalManagement.repository.PatientRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InsuranceServiceTest {

    @Mock
    private InsuranceRepository insuranceRepository;

    @Mock
    private PatientRepository patientRepository;

    private InsuranceService insuranceService;

    @BeforeEach
    void setUp() {
        insuranceService = new InsuranceService(
                insuranceRepository,
                patientRepository
        );
    }

    @Test
    void assignInsuranceToPatient_shouldSetBothSidesOfRelationship() {
        Patient patient = new Patient();
        patient.setId(12L);

        Insurance insurance = Insurance.builder()
                .policyNumber("POLICY-123")
                .provider("Example Health")
                .build();

        when(patientRepository.findById(12L))
                .thenReturn(Optional.of(patient));

        Patient result =
                insuranceService.assignInsuranceToPatient(insurance, 12L);

        assertSame(patient, result);
        assertSame(insurance, patient.getInsurance());
        assertSame(patient, insurance.getPatient());

        verify(patientRepository).findById(12L);
        verifyNoInteractions(insuranceRepository);
    }

    @Test
    void assignInsuranceToPatient_shouldThrowWhenPatientDoesNotExist() {
        Insurance insurance = Insurance.builder()
                .policyNumber("POLICY-123")
                .provider("Example Health")
                .build();

        when(patientRepository.findById(404L))
                .thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> insuranceService.assignInsuranceToPatient(insurance, 404L)
        );

        assertEquals("Patient not found with id 404", exception.getMessage());
        verifyNoInteractions(insuranceRepository);
    }

    @Test
    void disassociateInsuranceFromPatient_shouldClearPatientInsurance() {
        Patient patient = new Patient();
        patient.setId(12L);

        Insurance insurance = Insurance.builder()
                .policyNumber("POLICY-123")
                .provider("Example Health")
                .build();

        patient.setInsurance(insurance);
        insurance.setPatient(patient);

        when(patientRepository.findById(12L))
                .thenReturn(Optional.of(patient));

        Patient result =
                insuranceService.disassociateInsuranceFromPatient(12L);

        assertSame(patient, result);
        assertNull(patient.getInsurance());

        verify(patientRepository).findById(12L);
    }

    @Test
    void disassociateInsuranceFromPatient_shouldThrowWhenPatientDoesNotExist() {
        when(patientRepository.findById(404L))
                .thenReturn(Optional.empty());

        assertThrows(
                EntityNotFoundException.class,
                () -> insuranceService.disassociateInsuranceFromPatient(404L)
        );
    }
}
