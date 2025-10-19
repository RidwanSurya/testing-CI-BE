package com.example.wandoor.repository;

import com.example.wandoor.model.entity.SplitBillMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SplitBillMemberRepository extends JpaRepository<SplitBillMember, String> {
    List<SplitBillMember> findSplitBillById(String splitBillId);
}
