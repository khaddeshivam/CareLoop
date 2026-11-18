package com.example.hospitalManagement.services;

import com.example.hospitalManagement.dto.PatientProfileResponseDto;
import com.example.hospitalManagement.dto.PatientResponseDto;
import com.example.hospitalManagement.entities.Patient;
import com.example.hospitalManagement.entities.User;
import com.example.hospitalManagement.repository.PatientRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;
    private final ModelMapper modelMapper;

//    @Transactional
//    public Patient getPatientById(int Id){
//
//        Patient p1 = patientRepository.findById(Id).orElseThrow();
//

    /// /        Patient p2 = patientRepository.findById(Id).orElseThrow();
//
//        return p1;
//    }
    @Transactional
    public PatientProfileResponseDto getPatientById(Long id) {

        Patient patient = patientRepository.findById(id)
                .orElseThrow();

        return PatientProfileResponseDto.builder()
                .id(patient.getId())
                .firstName(patient.getFirstName())
                .lastName(patient.getLastName())
                .dateOfBirth(patient.getDateOfBirth())
                .gender(patient.getGender())
                .bloodGroup(patient.getBloodGroup())
                .phoneNumber(patient.getPhoneNumber())
                .build();
    }

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public PatientProfileResponseDto getMyPatientProfile(User user) {

        Patient patient = patientRepository.findByUser_id(user.getId())
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Patient profile not found for authenticated user"
                        )
                );

        PatientProfileResponseDto response = new PatientProfileResponseDto();

        response.setId(patient.getId());
        response.setFirstName(patient.getFirstName());
        response.setLastName(patient.getLastName());
        response.setDateOfBirth(patient.getDateOfBirth());
        response.setGender(patient.getGender());
        response.setBloodGroup(patient.getBloodGroup());
        response.setPhoneNumber(patient.getPhoneNumber());

        return response;
    }

    public List<PatientResponseDto> getAllPatients(Integer pageNumber, Integer pageSize) {
        return patientRepository.findAllPatients(PageRequest.of(pageNumber, pageSize))
                .stream()
                .map(patient -> modelMapper.map(patient, PatientResponseDto.class))
                .collect(Collectors.toList());
    }
}
