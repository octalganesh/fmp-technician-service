package com.octal.fsm.service;


import com.octal.fsm.dto.PageItem;
import com.octal.fsm.dto.TechnicianDto;
import com.octal.fsm.entities.Technician;
import com.octal.fsm.entities.enums.Gender;
import com.octal.fsm.exceptions.CodeException;
import com.octal.fsm.exceptions.ErrorCode;
import com.octal.fsm.models.request.PageRequest;
import com.octal.fsm.repositories.TechnicianRepository;
import com.octal.fsm.service.impl.TechnicianServiceImpl;
import com.octal.fsm.specification.SpecificationFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TechnicianServiceImplTest {

    @Mock
    private TechnicianRepository technicianRepository;

    @Mock
    private SpecificationFactory<Technician> technicianSpecificationFactory;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private TechnicianServiceImpl technicianService;

    private TechnicianDto.Add addTechnicianDto;
    private Technician technician;
    private String technicianId;

    @BeforeEach
    void setUp() {
        technicianId = UUID.randomUUID().toString();
        
        // Setup Add DTO
        addTechnicianDto = new TechnicianDto.Add(
            null, // id - null for new technician
            "John Doe",
            "john.doe@example.com",
            "9876543210",
            null, // employeeId - will be generated
            null, // profilePicture
            "123 Main St, City, State",
            true,
            Gender.MALE
        );
        
        // Setup Technician entity
        technician = new Technician();
        technician.setUuid(technicianId);
        technician.setName("John Doe");
        technician.setEmail("john.doe@example.com");
        technician.setMobileNumber("9876543210");
        technician.setEmployeeId("EMP001");
        technician.setAddress("123 Main St, City, State");
        technician.setGender(Gender.MALE);
        technician.setActive(true);
        technician.setDeleted(false);
        technician.setCreatedAt(LocalDateTime.now());
        technician.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void addTechnician_WhenNewTechnician_ShouldCreateSuccessfully() throws CodeException {
        // Given
        when(technicianRepository.existsByMobileNumber(anyString())).thenReturn(false);
        when(technicianRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(technicianRepository.save(any(Technician.class))).thenReturn(technician);

        // When
        String result = technicianService.addTechnician(addTechnicianDto);

        // Then
        assertNotNull(result);
        assertEquals(technicianId, result);
        verify(technicianRepository).existsByMobileNumber("9876543210");
        verify(technicianRepository).existsByEmail("john.doe@example.com");
        verify(passwordEncoder).encode("technician@123");
        verify(technicianRepository).save(any(Technician.class));
    }

    @Test
    void addTechnician_WhenNameIsEmpty_ShouldThrowCodeException() {
        // Given
        addTechnicianDto.setName("");

        // When & Then
        CodeException exception = assertThrows(CodeException.class, 
            () -> technicianService.addTechnician(addTechnicianDto));
        
        assertEquals("name is required", exception.getMessage());
        assertEquals(ErrorCode.COMMON, exception.getCode());
    }

    @Test
    void addTechnician_WhenMobileNumberIsEmpty_ShouldThrowCodeException() {
        // Given
        addTechnicianDto.setMobileNumber("");

        // When & Then
        CodeException exception = assertThrows(CodeException.class, 
            () -> technicianService.addTechnician(addTechnicianDto));
        
        assertEquals("mobile number is required", exception.getMessage());
        assertEquals(ErrorCode.COMMON, exception.getCode());
    }

    @Test
    void addTechnician_WhenEmailIsEmpty_ShouldThrowCodeException() {
        // Given
        addTechnicianDto.setEmail("");

        // When & Then
        CodeException exception = assertThrows(CodeException.class, 
            () -> technicianService.addTechnician(addTechnicianDto));
        
        assertEquals("email id is required", exception.getMessage());
        assertEquals(ErrorCode.COMMON, exception.getCode());
    }

    @Test
    void addTechnician_WhenAddressIsEmpty_ShouldThrowCodeException() {
        // Given
        addTechnicianDto.setAddress("");

        // When & Then
        CodeException exception = assertThrows(CodeException.class, 
            () -> technicianService.addTechnician(addTechnicianDto));
        
        assertEquals("address is required", exception.getMessage());
        assertEquals(ErrorCode.COMMON, exception.getCode());
    }

    @Test
    void addTechnician_WhenGenderIsNull_ShouldThrowCodeException() {
        // Given
        addTechnicianDto.setGender(null);

        // When & Then
        CodeException exception = assertThrows(CodeException.class, 
            () -> technicianService.addTechnician(addTechnicianDto));
        
        assertEquals("gender is required", exception.getMessage());
        assertEquals(ErrorCode.COMMON, exception.getCode());
    }

    @Test
    void addTechnician_WhenMobileNumberExists_ShouldThrowCodeException() {
        // Given
        when(technicianRepository.existsByMobileNumber(anyString())).thenReturn(true);

        // When & Then
        CodeException exception = assertThrows(CodeException.class, 
            () -> technicianService.addTechnician(addTechnicianDto));
        
        assertEquals("Technician with mobile number 9876543210 already exists", exception.getMessage());
        assertEquals(ErrorCode.RECORD_NOT_FOUND, exception.getCode());
    }

    @Test
    void addTechnician_WhenEmailExists_ShouldThrowCodeException() {
        // Given
        when(technicianRepository.existsByMobileNumber(anyString())).thenReturn(false);
        when(technicianRepository.existsByEmail(anyString())).thenReturn(true);

        // When & Then
        CodeException exception = assertThrows(CodeException.class, 
            () -> technicianService.addTechnician(addTechnicianDto));
        
        assertEquals("Technician with email john.doe@example.com already exists", exception.getMessage());
        assertEquals(ErrorCode.RECORD_NOT_FOUND, exception.getCode());
    }

    @Test
    void addTechnician_WhenUpdatingExistingTechnician_ShouldUpdateSuccessfully() throws CodeException {
        // Given
        addTechnicianDto.setId(technicianId);
        addTechnicianDto.setName("Updated Name");
        
        when(technicianRepository.findByUuid(technicianId)).thenReturn(Optional.of(technician));
        when(technicianRepository.findByMobileNumber(anyString())).thenReturn(Optional.empty());
        when(technicianRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(technicianRepository.save(any(Technician.class))).thenReturn(technician);

        // When
        String result = technicianService.addTechnician(addTechnicianDto);

        // Then
        assertNotNull(result);
        assertEquals(technicianId, result);
        verify(technicianRepository).findByUuid(technicianId);
        verify(technicianRepository, never()).existsByMobileNumber(anyString());
        verify(technicianRepository, never()).existsByEmail(anyString());
    }

    @Test
    void deleteById_WhenTechnicianExists_ShouldMarkAsDeleted() throws CodeException {
        // Given
        when(technicianRepository.findByUuid(technicianId)).thenReturn(Optional.of(technician));
        when(technicianRepository.save(any(Technician.class))).thenReturn(technician);

        // When
        Boolean result = technicianService.deleteById(technicianId);

        // Then
        assertTrue(result);
        verify(technicianRepository).findByUuid(technicianId);
        verify(technicianRepository).save(technician);
        assertTrue(technician.isDeleted());
    }

    @Test
    void deleteById_WhenTechnicianNotFound_ShouldThrowCodeException() {
        // Given
        when(technicianRepository.findByUuid(technicianId)).thenReturn(Optional.empty());

        // When & Then
        CodeException exception = assertThrows(CodeException.class, 
            () -> technicianService.deleteById(technicianId));
        
        assertEquals("technician not Found!", exception.getMessage());
        assertEquals(ErrorCode.RECORD_NOT_FOUND, exception.getCode());
    }

    @Test
    void getTechnicianByUuid_WhenTechnicianExists_ShouldReturnTechnicianList() throws CodeException {
        // Given
        when(technicianRepository.findByUuid(technicianId)).thenReturn(Optional.of(technician));

        // When
        TechnicianDto.list result = technicianService.getTechnicianByUuid(technicianId);

        // Then
        assertNotNull(result);
        assertEquals(technician.getName(), result.getName());
        assertEquals(technician.getEmail(), result.getEmail());
        assertEquals(technician.getMobileNumber(), result.getMobileNumber());
        verify(technicianRepository).findByUuid(technicianId);
    }

    @Test
    void getTechnicianByUuid_WhenTechnicianNotFound_ShouldThrowCodeException() {
        // Given
        when(technicianRepository.findByUuid(technicianId)).thenReturn(Optional.empty());

        // When & Then
        CodeException exception = assertThrows(CodeException.class, 
            () -> technicianService.getTechnicianByUuid(technicianId));
        
        assertEquals("technician not Found!", exception.getMessage());
        assertEquals(ErrorCode.RECORD_NOT_FOUND, exception.getCode());
    }

    @Test
    void changeStatus_WhenTechnicianExists_ShouldToggleStatus() throws CodeException {
        // Given
        technician.setActive(true);
        when(technicianRepository.findByUuid(technicianId)).thenReturn(Optional.of(technician));
        when(technicianRepository.save(any(Technician.class))).thenReturn(technician);

        // When
        Boolean result = technicianService.changeStatus(technicianId);

        // Then
        assertTrue(result);
        assertFalse(technician.getActive()); // Should be toggled to false
        verify(technicianRepository).findByUuid(technicianId);
        verify(technicianRepository).save(technician);
    }

    @Test
    void changeStatus_WhenTechnicianNotFound_ShouldThrowCodeException() {
        // Given
        when(technicianRepository.findByUuid(technicianId)).thenReturn(Optional.empty());

        // When & Then
        CodeException exception = assertThrows(CodeException.class, 
            () -> technicianService.changeStatus(technicianId));
        
        assertEquals("technician not Found!", exception.getMessage());
        assertEquals(ErrorCode.RECORD_NOT_FOUND, exception.getCode());
    }

    @Test
    void getTechnicianByEmailId_WhenTechnicianExists_ShouldReturnTechnician() {
        // Given
        String email = "john.doe@example.com";
        when(technicianRepository.findByEmail(email)).thenReturn(Optional.of(technician));

        // When
        Technician result = technicianService.getTechnicianByEmailId(email);

        // Then
        assertNotNull(result);
        assertEquals(technician.getEmail(), result.getEmail());
        assertEquals(technician.getName(), result.getName());
        verify(technicianRepository).findByEmail(email);
    }

    @Test
    void getTechnicianByEmailId_WhenTechnicianNotFound_ShouldReturnNull() {
        // Given
        String email = "nonexistent@example.com";
        when(technicianRepository.findByEmail(email)).thenReturn(Optional.empty());

        // When
        Technician result = technicianService.getTechnicianByEmailId(email);

        // Then
        assertNull(result);
        verify(technicianRepository).findByEmail(email);
    }

    @Test
    void getAllTechnician_ShouldReturnPagedResults() {
        // Given
        PageRequest.List listRequest = new PageRequest.List();
        listRequest.setPageNumber(0);
        listRequest.setPageSize(10);
        
        Page<Technician> technicianPage = new PageImpl<>(Arrays.asList(technician));
        @SuppressWarnings("unchecked")
        Specification<Technician> mockSpec = mock(Specification.class);
        when(technicianSpecificationFactory.isEqual(anyString(), any())).thenReturn(mockSpec);
        when(technicianRepository.findAll(mockSpec, any(Pageable.class))).thenReturn(technicianPage);

        // When
        PageItem<TechnicianDto.list> result = technicianService.getAllTechnician(listRequest);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        assertEquals(1, result.getTotalItems());
        verify(technicianRepository).findAll(mockSpec, any(Pageable.class));
    }
}