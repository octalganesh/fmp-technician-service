package com.octal.fsm.repositories;

import com.octal.fsm.entities.Technician;
import com.octal.fsm.entities.enums.Gender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class TechnicianRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TechnicianRepository technicianRepository;

    private Technician technician1;
    private Technician technician2;

    @BeforeEach
    void setUp() {
        // Create test technicians
        technician1 = new Technician();
        technician1.setName("John Doe");
        technician1.setEmail("john.doe@example.com");
        technician1.setMobileNumber("9876543210");
        technician1.setEmployeeId("EMP001");
        technician1.setAddress("123 Main St, City, State");
        technician1.setActive(true);
        technician1.setDeleted(false);
        technician1.setGender(Gender.MALE);
        technician1.setPassword("password123");
        technician1.setCreatedAt(LocalDateTime.now());
        technician1.setUpdatedAt(LocalDateTime.now());

        technician2 = new Technician();
        technician2.setName("Jane Smith");
        technician2.setEmail("jane.smith@example.com");
        technician2.setMobileNumber("9876543211");
        technician2.setEmployeeId("EMP002");
        technician2.setAddress("456 Oak Ave, Town, State");
        technician2.setActive(false);
        technician2.setDeleted(false);
        technician2.setGender(Gender.FEMALE);
        technician2.setPassword("password456");
        technician2.setCreatedAt(LocalDateTime.now());
        technician2.setUpdatedAt(LocalDateTime.now());

        // Persist entities
        entityManager.persistAndFlush(technician1);
        entityManager.persistAndFlush(technician2);
    }

    @Test
    void findByUuid_WhenTechnicianExists_ShouldReturnTechnician() {
        // When
        Optional<Technician> found = technicianRepository.findByUuid(technician1.getUuid());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("John Doe");
        assertThat(found.get().getEmail()).isEqualTo("john.doe@example.com");
    }

    @Test
    void findByUuid_WhenTechnicianDoesNotExist_ShouldReturnEmpty() {
        // When
        Optional<Technician> found = technicianRepository.findByUuid("non-existent-uuid");

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    void findByEmail_WhenEmailExists_ShouldReturnTechnician() {
        // When
        Optional<Technician> found = technicianRepository.findByEmail("john.doe@example.com");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("John Doe");
        assertThat(found.get().getUuid()).isEqualTo(technician1.getUuid());
    }

    @Test
    void findByEmail_WhenEmailDoesNotExist_ShouldReturnEmpty() {
        // When
        Optional<Technician> found = technicianRepository.findByEmail("nonexistent@example.com");

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    void existsByEmail_WhenEmailExists_ShouldReturnTrue() {
        // When
        boolean exists = technicianRepository.existsByEmail("john.doe@example.com");

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void existsByEmail_WhenEmailDoesNotExist_ShouldReturnFalse() {
        // When
        boolean exists = technicianRepository.existsByEmail("nonexistent@example.com");

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    void findByMobileNumber_WhenMobileNumberExists_ShouldReturnTechnician() {
        // When
        Optional<Technician> found = technicianRepository.findByMobileNumber("9876543210");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("John Doe");
        assertThat(found.get().getEmail()).isEqualTo("john.doe@example.com");
    }

    @Test
    void findByMobileNumber_WhenMobileNumberDoesNotExist_ShouldReturnEmpty() {
        // When
        Optional<Technician> found = technicianRepository.findByMobileNumber("0000000000");

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    void existsByMobileNumber_WhenMobileNumberExists_ShouldReturnTrue() {
        // When
        boolean exists = technicianRepository.existsByMobileNumber("9876543210");

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void existsByMobileNumber_WhenMobileNumberDoesNotExist_ShouldReturnFalse() {
        // When
        boolean exists = technicianRepository.existsByMobileNumber("0000000000");

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    void findByName_WhenNameExists_ShouldReturnTechnician() {
        // When
        Optional<Technician> found = technicianRepository.findByName("John Doe");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("john.doe@example.com");
        assertThat(found.get().getMobileNumber()).isEqualTo("9876543210");
    }

    @Test
    void existsByName_WhenNameExists_ShouldReturnTrue() {
        // When
        boolean exists = technicianRepository.existsByName("John Doe");

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void existsByName_WhenNameDoesNotExist_ShouldReturnFalse() {
        // When
        boolean exists = technicianRepository.existsByName("Non Existent");

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    void findByIsActive_WhenActive_ShouldReturnActiveTechnicians() {
        // When
        List<Technician> activeTechnicians = technicianRepository.findByIsActive(true);

        // Then
        assertThat(activeTechnicians).hasSize(1);
        assertThat(activeTechnicians.get(0).getName()).isEqualTo("John Doe");
        assertThat(activeTechnicians.get(0).getActive()).isTrue();
    }

    @Test
    void findByIsActive_WhenInactive_ShouldReturnInactiveTechnicians() {
        // When
        List<Technician> inactiveTechnicians = technicianRepository.findByIsActive(false);

        // Then
        assertThat(inactiveTechnicians).hasSize(1);
        assertThat(inactiveTechnicians.get(0).getName()).isEqualTo("Jane Smith");
        assertThat(inactiveTechnicians.get(0).getActive()).isFalse();
    }

    @Test
    void findByNameContainingOrEmailContaining_WhenSearchingByName_ShouldFindTechnician() {
        // When
        List<Technician> found = technicianRepository.findByNameContainingOrEmailContaining("John", "");

        // Then
        assertThat(found).hasSize(1);
        assertThat(found.get(0).getName()).isEqualTo("John Doe");
    }

    @Test
    void findByNameContainingOrEmailContaining_WhenSearchingByEmail_ShouldFindTechnician() {
        // When
        List<Technician> found = technicianRepository.findByNameContainingOrEmailContaining("", "jane.smith");

        // Then
        assertThat(found).hasSize(1);
        assertThat(found.get(0).getName()).isEqualTo("Jane Smith");
    }

    @Test
    void findByJoinDateBetween_WhenDateRangeProvided_ShouldFindTechniciansInRange() {
        // Given
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(1);

        // When
        List<Technician> found = technicianRepository.findByJoinDateBetween(start, end);

        // Then - Should return no results since joinDate was not set in our test data
        assertThat(found).isEmpty();
    }

    @Test
    void save_WhenValidTechnician_ShouldPersistTechnician() {
        // Given
        Technician newTechnician = new Technician();
        newTechnician.setName("Bob Wilson");
        newTechnician.setEmail("bob.wilson@example.com");
        newTechnician.setMobileNumber("9876543212");
        newTechnician.setEmployeeId("EMP003");
        newTechnician.setAddress("789 Pine Rd, Village, State");
        newTechnician.setActive(true);
        newTechnician.setDeleted(false);
        newTechnician.setGender(Gender.MALE);
        newTechnician.setPassword("password789");
        newTechnician.setCreatedAt(LocalDateTime.now());
        newTechnician.setUpdatedAt(LocalDateTime.now());

        // When
        Technician saved = technicianRepository.save(newTechnician);

        // Then
        assertThat(saved.getUuid()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Bob Wilson");
        assertThat(saved.getEmail()).isEqualTo("bob.wilson@example.com");

        // Verify it's persisted
        Optional<Technician> found = technicianRepository.findByUuid(saved.getUuid());
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Bob Wilson");
    }

    @Test
    void delete_WhenTechnicianExists_ShouldRemoveTechnician() {
        // Given
        String technicianUuid = technician1.getUuid();

        // When
        technicianRepository.delete(technician1);

        // Then
        Optional<Technician> found = technicianRepository.findByUuid(technicianUuid);
        assertThat(found).isEmpty();
    }

    @Test
    void findAll_ShouldReturnAllTechnicians() {
        // When
        List<Technician> allTechnicians = technicianRepository.findAll();

        // Then
        assertThat(allTechnicians).hasSize(2);
        assertThat(allTechnicians).extracting(Technician::getName)
                .containsExactlyInAnyOrder("John Doe", "Jane Smith");
    }
}