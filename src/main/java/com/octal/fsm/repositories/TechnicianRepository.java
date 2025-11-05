package com.octal.fsm.repositories;

import com.octal.fsm.entities.MultiUserDeviceDetails;
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
    List<Technician> findByNameContainingOrEmailContaining(String name, String email);
    List<Technician> findByJoinDateBetween(java.time.LocalDateTime start, java.time.LocalDateTime end);

    Optional<Technician> findByUuid(String id);
    boolean existsByName(String name);
    Optional<Technician>  findByName(String name);
    Optional<Technician>  findByEmail(String name);

    boolean existsByMobileNumber(String mobileNumber);

    boolean existsByEmail(String email);

    Optional<Technician> findByMobileNumber(String mobileNumber);

    Optional<Technician> findByEmployeeId(String employeeId);

    // For ALL_USER
    @Query("SELECT t.multiUserDeviceDetails FROM Technician t " +
            "WHERE t.multiUserDeviceDetails.deviceToken IS NOT NULL " +
            "AND t.multiUserDeviceDetails.deviceToken <> '' " +
            "AND t.multiUserDeviceDetails.deviceType IS NOT NULL " +
            "AND t.multiUserDeviceDetails.deviceType <> ''")
    List<MultiUserDeviceDetails> findByDeviceTokenNotNullAndDeviceTypeNotNullAndDeviceTokenNot(@Param("deviceToken") String deviceToken);

    // For ALL_ANDROID_USER or ALL_IOS_USER
    @Query("SELECT t.multiUserDeviceDetails FROM Technician t " +
            "WHERE LOWER(t.multiUserDeviceDetails.deviceType) = LOWER(:deviceType) " +
            "AND t.multiUserDeviceDetails.deviceToken IS NOT NULL " +
            "AND t.multiUserDeviceDetails.deviceToken <> :deviceToken")
    List<MultiUserDeviceDetails> findByDeviceTypeIgnoreCaseAndDeviceTokenIsNotNullAndDeviceTokenNot(@Param("deviceType") String deviceType,
                                                                                                    @Param("deviceToken") String deviceToken);

    // For PARTICULAR_USER
    @Query("SELECT t.multiUserDeviceDetails FROM Technician t WHERE t.uuid IN :userIds")
    List<MultiUserDeviceDetails> findByUserIdIn(@Param("userIds") List<String> userIds);
}

