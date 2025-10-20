package com.example.wandoor.repository;

import com.example.wandoor.model.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, String> {
    List<Account> findByUserIdAndCif(String userId, String cif);
}
