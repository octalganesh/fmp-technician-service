package com.octal.fsm.repositories;


import com.octal.fsm.entities.Technician;
import com.octal.fsm.entities.UserOtpVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserVerificationRepository extends JpaRepository<UserOtpVerification, String> {

    Optional<UserOtpVerification> findByOtp(String otp);

    @Query("select uv from #{#entityName} uv where uv.type=:type and uv.user.id=:userId")
    Optional<UserOtpVerification> findByTypeAndUserId(@Param("type") UserOtpVerification.Types type, @Param("userId") String userId);

    Optional<UserOtpVerification> findByTypeAndUser(UserOtpVerification.Types type, Technician user);

    Optional<UserOtpVerification> findByToken(String token);
}