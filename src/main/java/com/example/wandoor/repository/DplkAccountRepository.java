package com.example.wandoor.repository;

import com.example.wandoor.model.entity.DplkAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DplkAccountRepository extends JpaRepository<DplkAccount, String> {
    List<DplkAccount> findAllByUserIdAndCif(String userId, String cif);
}
