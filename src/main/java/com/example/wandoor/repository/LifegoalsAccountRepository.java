package com.example.wandoor.repository;

import com.example.wandoor.model.entity.LifegoalsAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LifegoalsAccountRepository extends JpaRepository<LifegoalsAccount, String> {
    List<LifegoalsAccount> findByUserIdAndCif(String userId, String cif);
}
