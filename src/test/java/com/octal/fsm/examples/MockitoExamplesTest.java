package com.octal.fsm.examples;

import com.octal.fsm.entities.Technician;
import com.octal.fsm.entities.enums.Gender;
import com.octal.fsm.repositories.TechnicianRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * This class demonstrates various Mockito techniques and best practices
 * for testing in Spring Boot applications.
 */
@ExtendWith(MockitoExtension.class)
class MockitoExamplesTest {

    @Mock
    private TechnicianRepository technicianRepository;

    private Technician technician;

    @BeforeEach
    void setUp() {
        technician = new Technician();
        technician.setUuid("test-uuid");
        technician.setName("John Doe");
        technician.setEmail("john.doe@example.com");
        technician.setMobileNumber("9876543210");
        technician.setEmployeeId("EMP001");
        technician.setAddress("123 Main St");
        technician.setActive(true);
        technician.setDeleted(false);
        technician.setGender(Gender.MALE);
        technician.setPassword("encoded-password");
        technician.setCreatedAt(LocalDateTime.now());
        technician.setUpdatedAt(LocalDateTime.now());
    }

    /**
     * Example 1: Basic mocking with when().thenReturn()
     */
    @Test
    void example1_basicMocking() {
        // Given
        when(technicianRepository.findByEmail("john.doe@example.com"))
                .thenReturn(Optional.of(technician));

        // When
        Optional<Technician> result = technicianRepository.findByEmail("john.doe@example.com");

        // Then
        assertTrue(result.isPresent());
        assertEquals("John Doe", result.get().getName());
        
        // Verify that the method was called
        verify(technicianRepository).findByEmail("john.doe@example.com");
    }

    /**
     * Example 2: Mocking with ArgumentMatchers
     */
    @Test
    void example2_argumentMatchers() {
        // Given - Use any() matcher to match any string
        when(technicianRepository.findByEmail(anyString()))
                .thenReturn(Optional.of(technician));

        // When
        Optional<Technician> result1 = technicianRepository.findByEmail("any.email@example.com");
        Optional<Technician> result2 = technicianRepository.findByEmail("another.email@example.com");

        // Then
        assertTrue(result1.isPresent());
        assertTrue(result2.isPresent());
        
        // Verify with argument matchers
        verify(technicianRepository, times(2)).findByEmail(anyString());
    }

    /**
     * Example 3: Argument Captor - capturing method arguments
     */
    @Test
    void example3_argumentCaptor() {
        // Given
        ArgumentCaptor<Technician> technicianCaptor = ArgumentCaptor.forClass(Technician.class);
        when(technicianRepository.save(any(Technician.class))).thenReturn(technician);

        // When
        technicianRepository.save(technician);

        // Then
        verify(technicianRepository).save(technicianCaptor.capture());
        
        Technician capturedTechnician = technicianCaptor.getValue();
        assertEquals("John Doe", capturedTechnician.getName());
        assertEquals("john.doe@example.com", capturedTechnician.getEmail());
    }

    /**
     * Example 4: Multiple return values
     */
    @Test
    void example4_multipleReturnValues() {
        // Given - First call returns empty, second call returns the technician
        when(technicianRepository.findByEmail("john.doe@example.com"))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(technician));

        // When & Then
        Optional<Technician> firstCall = technicianRepository.findByEmail("john.doe@example.com");
        Optional<Technician> secondCall = technicianRepository.findByEmail("john.doe@example.com");

        assertFalse(firstCall.isPresent());
        assertTrue(secondCall.isPresent());
        
        verify(technicianRepository, times(2)).findByEmail("john.doe@example.com");
    }

    /**
     * Example 5: Throwing exceptions
     */
    @Test
    void example5_throwingExceptions() {
        // Given
        when(technicianRepository.findByEmail("invalid.email"))
                .thenThrow(new RuntimeException("Database connection failed"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> technicianRepository.findByEmail("invalid.email"));
        
        assertEquals("Database connection failed", exception.getMessage());
        verify(technicianRepository).findByEmail("invalid.email");
    }

    /**
     * Example 6: Verifying method invocations
     */
    @Test
    void example6_verifyingInvocations() {
        // Given
        when(technicianRepository.existsByEmail(anyString())).thenReturn(false);
        when(technicianRepository.save(any(Technician.class))).thenReturn(technician);

        // When
        technicianRepository.existsByEmail("test@example.com");
        technicianRepository.save(technician);

        // Then - Various verification examples
        verify(technicianRepository).existsByEmail("test@example.com");
        verify(technicianRepository, times(1)).save(technician);
        verify(technicianRepository, never()).deleteById(anyLong());
        verify(technicianRepository, atLeastOnce()).save(any(Technician.class));
        
        // Verify no more interactions
        verifyNoMoreInteractions(technicianRepository);
    }

    /**
     * Example 7: Mocking collections
     */
    @Test
    void example7_mockingCollections() {
        // Given
        Technician technician2 = new Technician();
        technician2.setName("Jane Smith");
        technician2.setEmail("jane.smith@example.com");
        
        List<Technician> technicians = Arrays.asList(technician, technician2);
        when(technicianRepository.findByIsActive(true)).thenReturn(technicians);

        // When
        List<Technician> result = technicianRepository.findByIsActive(true);

        // Then
        assertEquals(2, result.size());
        assertEquals("John Doe", result.get(0).getName());
        assertEquals("Jane Smith", result.get(1).getName());
        
        verify(technicianRepository).findByIsActive(true);
    }

    /**
     * Example 8: doAnswer for complex mocking scenarios
     */
    @Test
    void example8_doAnswer() {
        // Given - Mock save method to modify the entity before returning
        when(technicianRepository.save(any(Technician.class)))
                .thenAnswer(invocation -> {
                    Technician arg = invocation.getArgument(0);
                    arg.setUpdatedAt(LocalDateTime.now());
                    return arg;
                });

        // When
        Technician result = technicianRepository.save(technician);

        // Then
        assertNotNull(result.getUpdatedAt());
        verify(technicianRepository).save(technician);
    }

    /**
     * Example 9: Spy vs Mock
     */
    @Test
    void example9_spyVsMock() {
        // Spy allows you to call real methods on the object
        // while still allowing you to stub specific methods
        
        Technician realTechnician = new Technician();
        Technician spiedTechnician = spy(realTechnician);
        
        // You can stub methods on the spy
        doReturn("Mocked Name").when(spiedTechnician).getName();
        
        // When
        String name = spiedTechnician.getName();
        
        // Then
        assertEquals("Mocked Name", name);
        
        // Real methods are called for non-stubbed methods
        spiedTechnician.setEmail("test@example.com");
        assertEquals("test@example.com", spiedTechnician.getEmail());
    }

    /**
     * Example 10: Partial mocking with doCallRealMethod
     */
    @Test
    void example10_partialMocking() {
        // Create a mock
        Technician mockTechnician = mock(Technician.class);
        
        // Call real method for specific method
        doCallRealMethod().when(mockTechnician).setName(anyString());
        doCallRealMethod().when(mockTechnician).getName();
        
        // When
        mockTechnician.setName("Real Name");
        String name = mockTechnician.getName();
        
        // Then
        assertEquals("Real Name", name);
    }
}