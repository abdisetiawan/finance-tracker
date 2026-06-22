package com.kamu.finance_tracker.repository;

import com.kamu.finance_tracker.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {

    // Cek apakah budget sudah ada untuk kombinasi ini
    Boolean existsByUserIdAndCategoryIdAndMonthAndYear(
            Long userId, Long categoryId, Integer month, Integer year);

    // Ambil semua budget user
    List<Budget> findByUserId(Long userId);

    // Ambil semua budget user bulan tertentu
    List<Budget> findByUserIdAndMonthAndYear(
            Long userId, Integer month, Integer year);

    // Cek ownership
    Optional<Budget> findByIdAndUserId(Long id, Long userId);

}
