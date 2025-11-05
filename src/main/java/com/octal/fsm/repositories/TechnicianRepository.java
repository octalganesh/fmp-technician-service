package com.octal.fsm.repositories;

import com.octal.fsm.entities.Technician;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TechnicianRepository extends JpaRepository<Technician, Long>, JpaSpecificationExecutor<Technician> {
    List<Technician> findByIsActiveAndTenantId(boolean active,Long tenantId);
    List<Technician> findByNameContainingOrEmailContainingAndTenantId(String name, String email,Long tenantId);
    List<Technician> findByJoinDateBetweenAndTenantId(java.time.LocalDateTime start, java.time.LocalDateTime end,Long tenantId);

    Optional<Technician> findByUuidAndTenantId(String id,Long tenantId);
    boolean existsByNameAndTenantId(String name,Long tenantId);
    Optional<Technician>  findByNameAndTenantId(String name,Long tenantId);
    Optional<Technician>  findByEmail(String name);

    boolean existsByMobileNumberAndTenantId(String mobileNumber,Long tenantId);

    boolean existsByEmailAndTenantId(String email,Long tenantId);

    Optional<Technician> findByMobileNumberAndTenantId(String mobileNumber,Long tenantId);

    Optional<Technician> findByEmployeeId (String employeeId );

    Optional<Technician> findByUuid(String id);
}

