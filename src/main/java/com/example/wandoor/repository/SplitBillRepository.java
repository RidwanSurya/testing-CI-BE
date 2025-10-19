package com.example.wandoor.repository;

import com.example.wandoor.model.entity.SplitBill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SplitBillRepository extends JpaRepository<SplitBill, String>  {
    List<SplitBill> findByUserIdAndCif(String userId, String cif);
}
