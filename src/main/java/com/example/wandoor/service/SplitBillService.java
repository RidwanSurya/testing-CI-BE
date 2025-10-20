package com.example.wandoor.service;

import com.example.wandoor.config.RequestContext;
import com.example.wandoor.model.entity.SplitBill;
import com.example.wandoor.model.entity.SplitBillMember;
import com.example.wandoor.model.response.SplitBillsListResponse;
import com.example.wandoor.repository.ProfileRepository;
import com.example.wandoor.repository.SplitBillMemberRepository;
import com.example.wandoor.repository.SplitBillRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Log4j2
public class SplitBillService {
    private final SplitBillRepository splitBillRepository;
    private final SplitBillMemberRepository splitBillMemberRepository;
    private final ProfileRepository profileRepository;

    public SplitBillsListResponse getAllSplitBill(){
        var userId = RequestContext.get().getUserId();
        var cif = RequestContext.get().getCif();

        var userExists = profileRepository.findByIdAndCif(userId, cif)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        List<SplitBill> splitBills = splitBillRepository.findByUserIdAndCif(userId, cif);

        List<SplitBillsListResponse.SplitBillData> splitBillDataList = splitBills.stream()
                .map(splitBill -> {
                    List<SplitBillMember> members = splitBillMemberRepository.findAllBySplitBillId(splitBill.getId());

                    BigDecimal totalBill = splitBill.getTotalAmount();
                    BigDecimal paidAmount = members.stream()
                            .filter(m -> m.getHasPaid() == 1)
                            .map(SplitBillMember::getAmountShare)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal remainingAmount = totalBill.subtract(paidAmount);
                    long countPaid = members.stream().filter(m -> m.getHasPaid() == 1).count();
                    long countUnpaid = members.size() - countPaid;

                    // Konversi member detail
                    List<SplitBillsListResponse.SplitBillData.SplitBillMemberDetail> memberDetails = members.stream()
                            .map(m -> new SplitBillsListResponse.SplitBillData.SplitBillMemberDetail(
                                    m.getMemberName(),
                                    m.getAmountShare(),
                                    m.getHasPaid()
                            ))
                            .toList();

                    return new SplitBillsListResponse.SplitBillData(
                            splitBill.getId(),
                            splitBill.getSplitBillTitle(),
                            splitBill.getCurrency(),
                            totalBill,
                            remainingAmount,
                            paidAmount,
                            (int) countPaid,
                            (int) countUnpaid,
                            memberDetails
                    );
                }).toList();

        List<SplitBillMember> splitBillMembers = splitBillMemberRepository.findAllBySplitBillId()
        return new SplitBillsListResponse();
    }
}
