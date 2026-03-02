package com.jys.smartbudget.controller;

import com.jys.smartbudget.dto.ApiResponse;
import com.jys.smartbudget.dto.BankAccountDto;
import com.jys.smartbudget.dto.ExpenseDTO;
import com.jys.smartbudget.dto.SearchRequest;
import com.jys.smartbudget.dto.StatisticsDTO;
import com.jys.smartbudget.exception.BusinessException;
import com.jys.smartbudget.exception.ErrorCode;
import com.jys.smartbudget.service.ExpenseService;
import com.jys.smartbudget.util.DateUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Validated          //@PathVariable이나 @RequestParam에 직접 붙인 제약 조건 사용시 필요
@RestController
@RequiredArgsConstructor
@RequestMapping("/expenses")
public class ExpenseController {

        private final RestTemplate restTemplate = new RestTemplate();
        private final ExpenseService expenseService;

        @PostMapping("/search")
        public ResponseEntity<ApiResponse<Map<String, Object>>> getExpenseWithBalance(
                @RequestBody SearchRequest searchRequest,
                HttpServletRequest req) {
        
        String userId = (String) req.getAttribute("userId");
        searchRequest.setUserId(userId);

        // 1. SmartBudget DB에서 지출 내역 조회 (페이징)
        List<ExpenseDTO> expenses = expenseService.getExpenses(searchRequest);

        BankAccountDto bankAccount = restTemplate.getForObject(
                "http://localhost:8081/api/v1/payments/accounts?userId=" + userId, 
                BankAccountDto.class
        );

        // 3. 결과 합치기
        Map<String, Object> result = new HashMap<>();
        result.put("expenses", expenses);
        result.put("accountInfo", bankAccount); // 현재 잔액 정보

        return ResponseEntity.ok(ApiResponse.success("내역 조회 성공", result));
        }



}
