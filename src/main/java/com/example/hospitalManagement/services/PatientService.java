package com.example.hospitalManagement.services;

import com.example.hospitalManagement.entities.Patient;
import com.example.hospitalManagement.repository.PatientRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;

    @Transactional
    public Patient getPatientById(int Id){

        Patient p1 = patientRepository.findById(Id).orElseThrow();

//        Patient p2 = patientRepository.findById(Id).orElseThrow();

        return p1;
    }
}
