package com.example.wandoor.service;

import com.example.wandoor.config.RequestContext;
import com.example.wandoor.model.entity.SplitBill;
import com.example.wandoor.model.entity.SplitBillMember;
import com.example.wandoor.model.request.SplitBillDetailRequest;
import com.example.wandoor.model.response.SplitBillDetailResponse;
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
import java.util.ArrayList;
import java.util.List;

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

        List<SplitBill> userSplitBills = splitBillRepository.findByUserIdAndCif(userId, cif);

        List<SplitBillsListResponse.SplitBillData> responseList = new ArrayList<>();

        for (SplitBill bill: userSplitBills) {
            List<SplitBillMember> members = splitBillMemberRepository.findAllBySplitBillId(bill.getId());

            BigDecimal paidAmount = BigDecimal.ZERO;
            BigDecimal remainingAmount = BigDecimal.ZERO;
            int countPaid = 0;
            int countUnpaid = 0;

            List<SplitBillsListResponse.SplitBillData.SplitBillMemberDetail> memberDetails = new ArrayList<>();

            for (SplitBillMember member: members){
                if (member.getHasPaid() != null && member.getHasPaid() == 1){
                    paidAmount = paidAmount.add(member.getAmountShare());
                    countPaid++;
                } else {
                    remainingAmount = remainingAmount.add(member.getAmountShare());
                    countUnpaid++;
                }

                // Tambahkan ke member detail
                SplitBillsListResponse.SplitBillData.SplitBillMemberDetail memberDetail =
                        new SplitBillsListResponse.SplitBillData.SplitBillMemberDetail(
                                member.getMemberName(),
                                member.getAmountShare(),
                                member.getHasPaid() != null && member.getHasPaid() == 1
                        );
                memberDetails.add(memberDetail);
            }
            // Buat Object SplitBillData
            SplitBillsListResponse.SplitBillData splitBillData =
                    new SplitBillsListResponse.SplitBillData(
                            bill.getId(),
                            bill.getSplitBillTitle(),
                            bill.getTransactionId(),
                            bill.getCurrency(),
                            bill.getTotalAmount(),
                            remainingAmount,
                            paidAmount,
                            countPaid,
                            countUnpaid,
                            memberDetails
                    );
            responseList.add(splitBillData);
        }


        return new SplitBillsListResponse(responseList);
    }
    public SplitBillDetailResponse getAllSplitBillMember(SplitBillDetailRequest request) {
        var userId = RequestContext.get().getUserId();
        var cif = RequestContext.get().getCif();

        var transaction = splitBillRepository.findByTransactionId(request.transactionId());
        if (transaction.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found");
        }

        var members = splitBillMemberRepository.findAllBySplitBillId(request.splitBillId());
        if (members.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Member not found");
        }

        // Buat list member response
        List<SplitBillDetailResponse.Data.Member> memberList = new ArrayList<>();
        for (SplitBillMember m : members) {
            BigDecimal hasPaid = BigDecimal.valueOf(m.getHasPaid());
            BigDecimal amountShare = m.getAmountShare();

            // Jika hasPaid >= amountShare → Paid, else Unpaid
            String status = (hasPaid.compareTo(amountShare) >= 0) ? "Paid" : "Unpaid";

            // Konversi tanggal ke String agar cocok dengan record
            String paymentDate = (m.getPaymentDate() != null)
                    ? m.getPaymentDate().toString()
                    : "-";

            memberList.add(new SplitBillDetailResponse.Data.Member(
                    m.getMemberName(),
                    amountShare,
                    status,
                    paymentDate
            ));
        }

        // Ambil data utama dari transaksi split bill
        var splitBill = transaction.get();

        SplitBillDetailResponse.Data data = new SplitBillDetailResponse.Data(
                splitBill.getId(),
                splitBill.getSplitBillTitle(),
                splitBill.getCurrency(),
                splitBill.getTotalAmount(),
                splitBill.getCreatedTime().toString(),
                splitBill.getTransactionId(),
                memberList
        );

        // Return response akhir
        return new SplitBillDetailResponse(
                "success",
                "Split Bill Detail Fetched Successfully",
                data
        );
    }

}
