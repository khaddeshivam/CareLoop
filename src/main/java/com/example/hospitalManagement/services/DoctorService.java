package com.example.hospitalManagement.services;

import com.example.hospitalManagement.dto.DoctorResponseDto;
import com.example.hospitalManagement.dto.OnBoardDoctorRequestDto;
import com.example.hospitalManagement.entities.Doctor;
import com.example.hospitalManagement.entities.User;
import com.example.hospitalManagement.entities.type.RoleType;
import com.example.hospitalManagement.repository.DoctorRepository;
import com.example.hospitalManagement.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public List<DoctorResponseDto> getAllDoctors(){
        return doctorRepository.findAll()
                .stream()
                .map(doctor -> modelMapper.map(doctor, DoctorResponseDto.class))
                .collect(Collectors.toList());
    }

    @Transactional
    public DoctorResponseDto onBoardNewDoctor(OnBoardDoctorRequestDto onBoardDoctorRequestDto){
        User user = userRepository.findById(onBoardDoctorRequestDto.getUserId()).orElseThrow();

        if(doctorRepository.existsByUser_Id(onBoardDoctorRequestDto.getUserId())){
            throw new IllegalArgumentException("Already a doctor");
        }

        Doctor doctor = Doctor.builder()
                .name(onBoardDoctorRequestDto.getName())
                .specialization(onBoardDoctorRequestDto.getSpecialization())
                .user(user)
                .build();

        user.getRoles().add(RoleType.DOCTOR);

        Doctor savedDoctor = doctorRepository.save(doctor);

        return modelMapper.map(savedDoctor, DoctorResponseDto.class);
    }
}
