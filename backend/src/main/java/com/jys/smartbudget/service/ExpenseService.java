package com.jys.smartbudget.service;

import com.jys.smartbudget.dto.ExpenseDTO;
import com.jys.smartbudget.dto.SearchRequest;
import com.jys.smartbudget.dto.StatisticsDTO;
import com.jys.smartbudget.mapper.ExpenseMapper;
import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseMapper expenseMapper;

    public List<ExpenseDTO> getExpenses(SearchRequest searchRequest) {
        return expenseMapper.getExpenses(searchRequest);
    }

    public List<StatisticsDTO> getExpenseStatistics(SearchRequest searchRequest) {
        return expenseMapper.getExpenseStatistics(searchRequest);
    }


    @Transactional
    public void updateExpense(ExpenseDTO expense) {
       int updatedCount =  expenseMapper.updateExpense(expense);

        if (updatedCount == 0) {
            throw new OptimisticLockException("이미 수정된 데이터입니다.");
        }
    }


}
