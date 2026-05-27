package com.kamu.finance_tracker.service;

import com.kamu.finance_tracker.dto.SummaryResponse;
import com.kamu.finance_tracker.dto.TransactionRequest;
import com.kamu.finance_tracker.entity.Category;
import com.kamu.finance_tracker.entity.Transaction;
import com.kamu.finance_tracker.entity.TransactionType;
import com.kamu.finance_tracker.entity.User;
import com.kamu.finance_tracker.repository.CategoryRepository;
import com.kamu.finance_tracker.repository.TransactionRepository;
import com.kamu.finance_tracker.repository.UserRepository;
import com.kamu.finance_tracker.dto.TransactionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public TransactionResponse create(Long userId, TransactionRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category tidak ditemukan"));

        Transaction transaction = Transaction.builder()
                .user(user)
                .category(category)
                .amount(request.getAmount())
                .type(request.getType())
                .note(request.getNote())
                .date(request.getDate())
                .build();

        Transaction saved = transactionRepository.save(transaction);
        return toResponse(saved);
    }

    public List<TransactionResponse> getAll(Long userId) {
        return transactionRepository.findByUserId(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public TransactionResponse update(Long userId, Long transactionId, TransactionRequest request) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaksi tidak ditemukan"));

        if (!transaction.getUser().getId().equals(userId)) {
            throw new RuntimeException("Transaksi bukan milik user ini");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category tidak ditemukan"));

        transaction.setCategory(category);
        transaction.setAmount(request.getAmount());
        transaction.setType(request.getType());
        transaction.setNote(request.getNote());
        transaction.setDate(request.getDate());

        Transaction saved = transactionRepository.save(transaction);
        return toResponse(saved);
    }

    public void delete(Long userId, Long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaksi tidak ditemukan"));

        if (!transaction.getUser().getId().equals(userId)) {
            throw new RuntimeException("Transaksi bukan milik user ini");
        }

        transactionRepository.delete(transaction);
    }

    private TransactionResponse toResponse(Transaction transaction) {
        return TransactionResponse.builder()
                .id(transaction.getId())
                .categoryId(transaction.getCategory().getId())
                .categoryName(transaction.getCategory().getName())
                .type(transaction.getType())
                .amount(transaction.getAmount())
                .note(transaction.getNote())
                .date(transaction.getDate())
                .createdAt(transaction.getCreatedAt())
                .build();
    }

    public Page<TransactionResponse> getAllPaginated(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("date").descending());
        return transactionRepository.findByUserId(userId, pageable)
                .map(this::toResponse);
    }

    public Page<TransactionResponse> filterByCategory(Long userId, Long categoryId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("date").descending());
        return transactionRepository.findByUserIdAndCategoryId(userId, categoryId, pageable)
                .map(this::toResponse);
    }

    public Page<TransactionResponse> filterByType(Long userId, TransactionType type, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("date").descending());
        return transactionRepository.findByUserIdAndType(userId, type, pageable)
                .map(this::toResponse);
    }

    public Page<TransactionResponse> filterByDateRange(Long userId, LocalDate startDate, LocalDate endDate, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("date").descending());
        return transactionRepository.findByUserIdAndDateBetween(userId, startDate, endDate, pageable)
                .map(this::toResponse);
    }

    public SummaryResponse getMonthlySummary(Long userId, int month, int year) {
        BigDecimal totalIncome = transactionRepository.sumIncomeByMonth(userId, month, year);
        BigDecimal totalExpense = transactionRepository.sumExpenseByMonth(userId, month, year);
        BigDecimal balance = totalIncome.subtract(totalExpense);

        return SummaryResponse.builder()
                .month(month)
                .year(year)
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .balance(balance)
                .build();
    }
}
