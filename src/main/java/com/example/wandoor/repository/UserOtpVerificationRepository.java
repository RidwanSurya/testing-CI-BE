package com.example.wandoor.repository;

import com.example.wandoor.model.entity.OtpVerification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserOtpVerificationRepository extends JpaRepository<OtpVerification, String> {

}
