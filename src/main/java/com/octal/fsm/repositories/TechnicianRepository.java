package com.octal.fsm.repositories;

import com.octal.fsm.entities.Technician;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TechnicianRepository extends JpaRepository<Technician, Long>, JpaSpecificationExecutor<Technician> {
    List<Technician> findByIsActive(boolean active);

    //List<Technician> findByNameContainingOrEmailContaining(String name, String email);
    @Query("SELECT t FROM Technician t " +
            "WHERE (:name IS NOT NULL AND LOWER(t.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
            "OR (:email IS NOT NULL AND LOWER(t.email) LIKE LOWER(CONCAT('%', :email, '%')))")
    List<Technician> findByNameContainingOrEmailContaining(
            @Param("name") String name,
            @Param("email") String email);

    List<Technician> findByJoinDateBetween(java.time.LocalDateTime start, java.time.LocalDateTime end);

    Optional<Technician> findByUuid(String id);

    boolean existsByName(String name);

    Optional<Technician> findByName(String name);

    Optional<Technician> findByEmail(String name);

    boolean existsByMobileNumber(String mobileNumber);

    boolean existsByEmail(String email);

    Optional<Technician> findByMobileNumber(String mobileNumber);

    Optional<Technician> findByEmployeeId(String employeeId);
}

