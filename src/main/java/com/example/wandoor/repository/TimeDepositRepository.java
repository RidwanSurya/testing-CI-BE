package com.example.wandoor.repository;

import com.example.wandoor.model.entity.TimeDepositAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TimeDepositRepository extends JpaRepository<TimeDepositAccount, String>  {
    List<TimeDepositAccount> findByUserIdAndCif(String userid, String cif);
}
