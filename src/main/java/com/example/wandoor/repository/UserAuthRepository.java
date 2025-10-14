package com.example.wandoor.repository;

import com.example.wandoor.model.entity.UserAuth;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAuthRepository extends JpaRepository<UserAuth, String> {
    Optional<UserAuth> findByUserId(String userId);
//    Optional<UserAuth> findByEmail(String emailAddress);
}
