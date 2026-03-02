package com.jys.smartbudget.dto;

import java.time.LocalDateTime;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseDTO {

    private Long id;                // PK

    private String userId;              


    // 1. 은행 연동 핵심 필드
    @NotBlank(message = "계좌번호는 필수입니다.")
    private String accountNumber;    // 결제된 계좌번호 (budget 매핑용)

    @NotBlank(message = "승인번호는 필수입니다.")
    private String approvalNo;       // 은행 승인번호 (중복 저장 방지 UNIQUE)

    @NotBlank(message = "가맹점명은 필수입니다.")
    private String merchantName;     // 가맹점 (기존 description 역할)

    // 2. 금액 및 날짜 정보
    @NotNull(message = "지출 금액은 필수입니다.")
    @Min(value = 0, message = "금액은 0원 이상이어야 합니다.")
    private Long amount;             // 지출 금액

    private Integer year;
    private Integer month;
    private Integer day;

    // 3. CMS 비즈니스 로직 필드
    private Long budgetId;           // 부서 예산 PK (accountNumber로 조회 후 세팅)
    private String category;         // 카테고리 (MEAL, CAFE 등)
    private String status;           // 처리 상태 (SUCCESS, PENDING 등)
    
    // 4. 무결성 및 관리 필드
    private Integer version;         // Optimistic Lock용 (낙관적 락)
    private LocalDateTime transactedAt; // 실제 결제 일시

}
