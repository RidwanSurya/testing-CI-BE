package com.example.wandoor.service;

import com.example.wandoor.model.entity.SplitBill;
import com.example.wandoor.model.entity.SplitBillMember;
import com.example.wandoor.model.request.SplitBillRequest;
import com.example.wandoor.model.response.SplitBillResponse;
import com.example.wandoor.repository.ProfileRepository;
import com.example.wandoor.repository.SplitBillMemberRepository;
import com.example.wandoor.repository.SplitBillRepository;
import com.example.wandoor.repository.UserAuthRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Builder
public class SplitBillService {
    private final SplitBillRepository splitBillRepository;
    private final SplitBillMemberRepository splitBillMemberRepository;
    private final UserAuthRepository userAuthRepository;
    private final ProfileRepository profileRepository;

    public List<SplitBillResponse> getSplitBills(SplitBillRequest req) {
        var userData = profileRepository.findByUserIdAndCif(req.userId(), req.customerId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT, "User Not Found"));

        List<SplitBill> splitBills = splitBillRepository.findByUserIdAndCif(
                userData.getId(),
                userData.getCif()
        );

        return splitBills.stream()
                .map(bill -> {
                    List<SplitBillMember> members = splitBillMemberRepository.findSplitBillById(bill.getId());
                    long unpaidCount = members.stream().filter(m -> !m.getHasPaid()).count();
                    long totalMembers = members.size();
                    double paidAmount = members.stream()
                            .filter(SplitBillMember::getHasPaid)
                            .mapToDouble(m -> m.getAmountShare().doubleValue())
                            .sum();
                    double unpaidAmount = bill.getTotalAmount().doubleValue() - paidAmount;
                    return new SplitBillResponse(
                            bill.getId(),
                            bill.getTransactionId(),
                            bill.getSplitBillTitle(),
                            totalMembers,
                            paidAmount,
                            unpaidAmount,
                            bill.getTotalAmount(),
                            bill.getCreatedTime(),
                            unpaidCount
                    );

                }).toList();

    }

}
