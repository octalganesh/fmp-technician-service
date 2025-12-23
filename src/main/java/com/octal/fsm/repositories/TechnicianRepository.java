package com.octal.fsm.repositories;

import com.octal.fsm.dto.TechnicianDto;
import com.octal.fsm.entities.MultiUserDeviceDetails;
import com.octal.fsm.entities.Technician;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TechnicianRepository extends JpaRepository<Technician, Long>, JpaSpecificationExecutor<Technician> {
    List<Technician> findByIsActiveAndTenantId(boolean active, Long tenantId);

    List<Technician> findByNameContainingOrEmailContainingAndTenantId(String name, String email, Long tenantId);

    List<Technician> findByJoinDateBetweenAndTenantId(java.time.LocalDateTime start, java.time.LocalDateTime end, Long tenantId);

    Optional<Technician> findByUuidAndTenantId(String id, Long tenantId);

    boolean existsByNameAndTenantId(String name, Long tenantId);

    Optional<Technician> findByNameAndTenantId(String name, Long tenantId);

    Optional<Technician> findByEmail(String name);

    boolean existsByMobileNumberAndTenantId(String mobileNumber, Long tenantId);

    boolean existsByEmailAndTenantId(String email, Long tenantId);

    Optional<Technician> findByMobileNumberAndTenantId(String mobileNumber, Long tenantId);

    Optional<Technician> findByEmployeeId(String employeeId);

    Optional<Technician> findByUuid(String id);

    List<Technician> findAllByTenantId(Long tenantId);

    // For ALL_USER
    @Query("SELECT t FROM Technician t " +
            "WHERE t.multiUserDeviceDetails.deviceToken IS NOT NULL " +
            "AND t.multiUserDeviceDetails.deviceToken <> '' " +
            "AND t.multiUserDeviceDetails.deviceType IS NOT NULL " +
            "AND t.multiUserDeviceDetails.deviceType <> ''")
    List<Technician> findByDeviceTokenNotNullAndDeviceTypeNotNullAndDeviceTokenNot(@Param("deviceToken") String deviceToken);

    // For ALL_ANDROID_USER or ALL_IOS_USER
    @Query("SELECT t FROM Technician t " +
            "WHERE LOWER(t.multiUserDeviceDetails.deviceType) = LOWER(:deviceType) " +
            "AND t.multiUserDeviceDetails.deviceToken IS NOT NULL " +
            "AND t.multiUserDeviceDetails.deviceToken <> :deviceToken")
    List<Technician> findByDeviceTypeIgnoreCaseAndDeviceTokenIsNotNullAndDeviceTokenNot(@Param("deviceType") String deviceType,
                                                                                                    @Param("deviceToken") String deviceToken);

    // For PARTICULAR_USER
    @Query("SELECT t FROM Technician t WHERE t.uuid IN :userIds")
    List<Technician> findByUserIdIn(@Param("userIds") List<String> userIds);

    long countByAndCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT t FROM Technician t WHERE t.uuid IN :ids")
    List<Technician> findByUuid(@Param("ids") List<String> ids);
}

