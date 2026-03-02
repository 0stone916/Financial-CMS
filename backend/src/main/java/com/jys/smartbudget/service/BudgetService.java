package com.jys.smartbudget.service;

import com.jys.smartbudget.dto.BudgetDTO;
import com.jys.smartbudget.mapper.BudgetMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@RequiredArgsConstructor
@Service
public class BudgetService {

    private final BudgetMapper budgetMapper;

    public void insertBudget(BudgetDTO budget) {
        budgetMapper.insertBudget(budget);
    }

    public List<BudgetDTO> selectBudgetsByConditionWithPaging(BudgetDTO condition) {
        return budgetMapper.selectBudgetsByConditionWithPaging(condition);
    }

    public void deleteBudgetByIdAndUserId(Long id, String userId) {
        budgetMapper.deleteBudgetByIdAndUserId(id, userId);
    }

    public Boolean existsByYearMonthCategory(BudgetDTO budget) {
        return budgetMapper.existsByYearMonthCategory(budget);
    }

    public BudgetDTO selectById(Long id) {
        return budgetMapper.selectById(id);
    }




}
