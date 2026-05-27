package com.kamu.finance_tracker.repository;

import com.kamu.finance_tracker.entity.Transaction;
import com.kamu.finance_tracker.entity.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUserId(Long userId);

    // Filter by kategori
    Page<Transaction> findByUserIdAndCategoryId(Long userId, Long categoryId, Pageable pageable);

    // Filter by type
    Page<Transaction> findByUserIdAndType(Long userId, TransactionType type, Pageable pageable);

    // Filter by rentang tanggal
    Page<Transaction> findByUserIdAndDateBetween(Long userId, LocalDate startDate, LocalDate endDate, Pageable pageable);

    // Semua dengan pagination
    Page<Transaction> findByUserId(Long userId, Pageable pageable);

    // Summary — total income per bulan
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
            "WHERE t.user.id = :userId AND t.type = 'INCOME' " +
            "AND MONTH(t.date) = :month AND YEAR(t.date) = :year")
    BigDecimal sumIncomeByMonth(@Param("userId") Long userId,
                                @Param("month") int month,
                                @Param("year") int year);

    // Summary — total expense per bulan
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
            "WHERE t.user.id = :userId AND t.type = 'EXPENSE' " +
            "AND MONTH(t.date) = :month AND YEAR(t.date) = :year")
    BigDecimal sumExpenseByMonth(@Param("userId") Long userId,
                                 @Param("month") int month,
                                 @Param("year") int year);
}
