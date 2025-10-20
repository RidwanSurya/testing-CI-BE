package com.example.wandoor.repository;

import java.util.Optional;
import com.example.wandoor.model.entity.TimeDepositAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DepositRepository extends JpaRepository<TimeDepositAccount, String>{
    List <TimeDepositAccount> getDepositByUserId (String userId);
}
