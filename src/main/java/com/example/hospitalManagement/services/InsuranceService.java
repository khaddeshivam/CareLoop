package com.example.hospitalManagement.services;

import com.example.hospitalManagement.entities.Insurance;
import com.example.hospitalManagement.entities.Patient;
import com.example.hospitalManagement.repository.InsuranceRepository;
import com.example.hospitalManagement.repository.PatientRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InsuranceService {

    private final InsuranceRepository insuranceRepository;
    private final PatientRepository patientRepository;

    @Transactional
    public Patient assignInsuranceToPatient(Insurance insurance, Integer patientId) {

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new EntityNotFoundException("Patient not found with id " + patientId));


        patient.setInsurance(insurance);
        insurance.setPatient(patient); // Bi-directional consistency maintenance

        return patient;
    }

    @Transactional
    public Patient disassociateInsuranceFromPatient(Integer patientId) {

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new EntityNotFoundException("Patient not found with id " + patientId));


        patient.setInsurance(null);

        return patient;
    }
}
