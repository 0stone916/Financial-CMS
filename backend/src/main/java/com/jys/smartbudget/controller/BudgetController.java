package com.jys.smartbudget.controller;

import com.jys.smartbudget.dto.ApiResponse;
import com.jys.smartbudget.dto.BankAccountDto;
import com.jys.smartbudget.exception.BusinessException;
import com.jys.smartbudget.exception.ErrorCode;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/budgets")
public class BudgetController {

        private final RestTemplate restTemplate = new RestTemplate();

    // 예산 조회
    @GetMapping("/search")
        public ResponseEntity<ApiResponse<BankAccountDto>> getBankBalance(HttpServletRequest req) {
                // 8081 은행 서버의 특정 유저 계좌 조회 엔드포인트
                String bankUrl = "http://localhost:8081/api/v1/payments/accounts?userId=" + (String) req.getAttribute("userId");
                
                log.info("userIduserIduserIduserIduserId", (String) req.getAttribute("userId"));

                try {
                        // RestTemplate을 통한 서버 간 통신 (Internal API Call)
                        BankAccountDto bankAccount = restTemplate.getForObject(bankUrl, BankAccountDto.class);

                        if (bankAccount == null) {
                                throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "연동된 은행 계좌를 찾을 수 없습니다.");
                        }
                        return ResponseEntity.ok(ApiResponse.success("은행 잔액 조회 성공", bankAccount));
                } catch (Exception e) {
                        // [본질] 외부 시스템(은행) 장애 시 시스템 가용성을 위한 예외 처리
                        log.error("Bank Server Communication Error: ", e);
                        throw new RuntimeException("은행 서버와 통신 중 오류가 발생했습니다.");
                }
        }       
}
