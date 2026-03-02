package com.jys.smartbudget.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BankAccountDto {
    private String accountNumber; // 계좌번호
    private String ownerName;     // 소유자명
    private long balance;         // 현재 잔액
    private String userId;        // 매핑된 사용자 ID (조회 조건)
}