package com.example.hospitalManagement;

import com.example.hospitalManagement.dto.PatientProfileResponseDto;
import com.example.hospitalManagement.entities.Patient;
import com.example.hospitalManagement.entities.User;
import com.example.hospitalManagement.repository.PatientRepository;
import com.example.hospitalManagement.services.PatientService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    private PatientService patientService;

    // Use a real ModelMapper and a mocked repository. This keeps the test
    // focused on PatientService behavior without starting Spring or PostgreSQL.

    private ModelMapper modelMapper;

    @BeforeEach
    void setUp() {
        modelMapper = new ModelMapper();
        patientService = new PatientService(patientRepository, modelMapper);
    }

    @Test
    void getPatientById_shouldReturnMappedProfile_whenPatientExists() {
        Patient patient = patient(
                10L, "Shivam", "Khadde",
                LocalDate.of(2004, 1, 15), "Male", "9876543210"
        );

        when(patientRepository.findById(10L))
                .thenReturn(Optional.of(patient));

        PatientProfileResponseDto result =
                patientService.getPatientById(10L);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals("Shivam", result.getFirstName());
        assertEquals("Khadde", result.getLastName());
        assertEquals(LocalDate.of(2004, 1, 15), result.getDateOfBirth());
        assertEquals("Male", result.getGender());
        assertEquals("9876543210", result.getPhoneNumber());

        verify(patientRepository).findById(10L);
        verifyNoMoreInteractions(patientRepository);
    }

    @Test
    void getPatientById_shouldThrow_whenPatientDoesNotExist() {
        when(patientRepository.findById(404L))
                .thenReturn(Optional.empty());

        assertThrows(
                java.util.NoSuchElementException.class,
                () -> patientService.getPatientById(404L)
        );

        verify(patientRepository).findById(404L);
    }

    @Test
    void getMyPatientProfile_shouldReturnProfileForAuthenticatedUser() {
        User user = new User();
        user.setId(25L);

        Patient patient = patient(
                25L, "Asha", "Patil",
                LocalDate.of(1998, 4, 12), "Female", "9000000001"
        );

        when(patientRepository.findByUser_id(25L))
                .thenReturn(Optional.of(patient));

        PatientProfileResponseDto result =
                patientService.getMyPatientProfile(user);

        assertNotNull(result);
        assertEquals(25L, result.getId());
        assertEquals("Asha", result.getFirstName());
        assertEquals("Patil", result.getLastName());
        assertEquals("Female", result.getGender());
        assertEquals("9000000001", result.getPhoneNumber());

        verify(patientRepository).findByUser_id(25L);
    }

    @Test
    void getMyPatientProfile_shouldThrowEntityNotFound_whenProfileDoesNotExist() {
        User user = new User();
        user.setId(999L);

        when(patientRepository.findByUser_id(999L))
                .thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> patientService.getMyPatientProfile(user)
        );

        assertEquals(
                "Patient profile not found for authenticated user",
                exception.getMessage()
        );

        verify(patientRepository).findByUser_id(999L);
    }

    private Patient patient(
            Long id,
            String firstName,
            String lastName,
            LocalDate dateOfBirth,
            String gender,
            String phoneNumber
    ) {
        Patient patient = new Patient();
        patient.setId(id);
        patient.setFirstName(firstName);
        patient.setLastName(lastName);
        patient.setDateOfBirth(dateOfBirth);
        patient.setGender(gender);
        patient.setPhoneNumber(phoneNumber);
        return patient;
    }
}
