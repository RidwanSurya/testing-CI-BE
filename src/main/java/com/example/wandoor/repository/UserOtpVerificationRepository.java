package com.example.wandoor.repository;

import com.example.wandoor.model.entity.OtpVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface UserOtpVerificationRepository extends JpaRepository<OtpVerification, String> {
    Optional<OtpVerification> findByIdAndIsUsed(String id, Integer isUsed);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        UPDATE OtpVerification o
           SET o.isUsed = 1
         WHERE o.id = :id
           AND o.isUsed = 0
           AND o.otpCode = :otpCode
           AND o.expiresAt > :now
    """)
    int consumeIfValid(@Param("id") String id,
                       @Param("otpCode") String otpCode,
                       @Param("now") LocalDateTime now);



}
