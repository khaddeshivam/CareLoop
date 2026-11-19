package com.example.hospitalManagement.services;

import com.example.hospitalManagement.dto.DoctorResponseDto;
import com.example.hospitalManagement.dto.OnBoardDoctorRequestDto;
import com.example.hospitalManagement.entities.Doctor;
import com.example.hospitalManagement.entities.User;
import com.example.hospitalManagement.entities.type.RoleType;
import com.example.hospitalManagement.repository.DoctorRepository;
import com.example.hospitalManagement.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DoctorServiceTest {

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private UserRepository userRepository;

    private DoctorService doctorService;

    @BeforeEach
    void setUp() {
        doctorService = new DoctorService(
                doctorRepository,
                userRepository,
                new ModelMapper()
        );
    }

    @Test
    void onBoardNewDoctor_shouldSaveDoctorAndAddDoctorRole() {
        User user = new User();
        user.setId(7L);
        user.setRoles(new HashSet<>());

        OnBoardDoctorRequestDto request = new OnBoardDoctorRequestDto();
        request.setUserId(7L);
        request.setName("Dr. Mehta");
        request.setSpecialization("Cardiology");

        when(userRepository.findById(7L)).thenReturn(Optional.of(user));
        when(doctorRepository.existsByUser_Id(7L)).thenReturn(false);
        when(doctorRepository.save(any(Doctor.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        DoctorResponseDto result = doctorService.onBoardNewDoctor(request);

        assertNotNull(result);
        assertEquals("Dr. Mehta", result.getName());
        assertEquals("Cardiology", result.getSpecialization());
        assertTrue(user.getRoles().contains(RoleType.DOCTOR));

        verify(userRepository).findById(7L);
        verify(doctorRepository).existsByUser_Id(7L);
        verify(doctorRepository).save(argThat(doctor ->
                "Dr. Mehta".equals(doctor.getName())
                        && "Cardiology".equals(doctor.getSpecialization())
                        && doctor.getUser() == user
        ));
    }

    @Test
    void onBoardNewDoctor_shouldRejectUserWhoIsAlreadyDoctor() {
        User user = new User();
        user.setId(7L);
        user.setRoles(new HashSet<>());

        OnBoardDoctorRequestDto request = new OnBoardDoctorRequestDto();
        request.setUserId(7L);
        request.setName("Dr. Mehta");
        request.setSpecialization("Cardiology");

        when(userRepository.findById(7L)).thenReturn(Optional.of(user));
        when(doctorRepository.existsByUser_Id(7L)).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> doctorService.onBoardNewDoctor(request)
        );

        assertEquals("Already a doctor", exception.getMessage());

        verify(doctorRepository, never()).save(any(Doctor.class));
    }

    @Test
    void onBoardNewDoctor_shouldFailWhenUserDoesNotExist() {
        OnBoardDoctorRequestDto request = new OnBoardDoctorRequestDto();
        request.setUserId(404L);

        when(userRepository.findById(404L)).thenReturn(Optional.empty());

        assertThrows(
                java.util.NoSuchElementException.class,
                () -> doctorService.onBoardNewDoctor(request)
        );

        verify(doctorRepository, never()).save(any(Doctor.class));
    }
}
