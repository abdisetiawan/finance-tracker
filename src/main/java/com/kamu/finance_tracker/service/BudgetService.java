package com.kamu.finance_tracker.service;

import com.kamu.finance_tracker.dto.BudgetRequest;
import com.kamu.finance_tracker.dto.BudgetResponse;
import com.kamu.finance_tracker.dto.BudgetStatusResponse;
import com.kamu.finance_tracker.dto.TransactionRequest;
import com.kamu.finance_tracker.dto.TransactionResponse;
import com.kamu.finance_tracker.entity.Budget;
import com.kamu.finance_tracker.entity.Category;
import com.kamu.finance_tracker.entity.Transaction;
import com.kamu.finance_tracker.entity.User;
import com.kamu.finance_tracker.repository.BudgetRepository;
import com.kamu.finance_tracker.repository.CategoryRepository;
import com.kamu.finance_tracker.repository.TransactionRepository;
import com.kamu.finance_tracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BudgetService {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;
    private final BudgetRepository budgetRepository;

    public BudgetResponse create(Long userId, BudgetRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    return new RuntimeException("User tidak ditemukan");
                });

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> {
                    return new RuntimeException("Category tidak ditemukan");
                });

        Boolean budget = budgetRepository.existsByUserIdAndCategoryIdAndMonthAndYear(user.getId(),request.getCategoryId(),request.getMonth(),request.getYear());
        if (budget){
            throw new RuntimeException("Budget sudah ada untuk kategori dan bulan ini");
        }

        Budget saved = budgetRepository.save(Budget.builder()
                .user(user)
                .category(category)
                .amount(request.getAmount())
                .month(request.getMonth())
                .year(request.getYear())
                .build());

        return toResponse(saved);
    }

    public List<BudgetResponse> getAll(Long userId) {
        return budgetRepository.findByUserId(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public BudgetResponse update(Long id, Long userId, BudgetRequest request) {
        Budget budget = budgetRepository.findByIdAndUserId(id,userId)
                .orElseThrow(() -> new RuntimeException("Budget tidak ditemukan"));

        budget.setAmount(request.getAmount());

        Budget saved = budgetRepository.save(budget);
        return toResponse(saved);
    }

    public void delete(Long userId, Long id) {

        Budget budget = budgetRepository.findByIdAndUserId(id,userId)
                .orElseThrow(() -> new RuntimeException("Budget tidak ditemukan"));

        budgetRepository.delete(budget);
    }

    public List<BudgetStatusResponse> checkBudget(Long userId, Integer month, Integer year) {
        List<BudgetStatusResponse> listBudget = new ArrayList<>();

        List<Budget> budgets = budgetRepository.findByUserIdAndMonthAndYear(userId, month, year);

        for (Budget budget : budgets) {
            // 1. Ambil total pengeluaran untuk kategori ini bulan ini
            BigDecimal totalSpent = transactionRepository.sumExpenseByMonthAndCategory(
                    userId,
                    budget.getCategory().getId(),
                    month,
                    year
            );

            // 2. Hitung remaining dan percentage
            BigDecimal budgetLimit = budget.getAmount();
            BigDecimal remaining = budgetLimit.subtract(totalSpent);
            double percentage = totalSpent.divide(budgetLimit, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .doubleValue();

            // 3. Tentuin status
            String status;
            if (percentage < 80) {
                status = "ON_TRACK";
            } else if (percentage <= 100) {
                status = "WARNING";
            } else {
                status = "OVER_BUDGET";
            }

            // 4. Build response
            BudgetStatusResponse data = BudgetStatusResponse.builder()
                    .categoryId(budget.getCategory().getId())
                    .categoryName(budget.getCategory().getName())
                    .budgetLimit(budgetLimit)
                    .totalSpent(totalSpent)
                    .remaining(remaining)
                    .percentage(percentage)
                    .status(status)
                    .build();

            listBudget.add(data);
        }

        return listBudget;
    }

    private BudgetResponse toResponse(Budget budget) {
        return BudgetResponse.builder()
                .id(budget.getId())
                .categoryId(budget.getCategory().getId())
                .categoryName(budget.getCategory().getName())
                .amount(budget.getAmount())
                .month(budget.getMonth())
                .year(budget.getYear())
                .createdAt(budget.getCreatedAt())
                .build();
    }
}
