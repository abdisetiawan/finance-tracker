package com.kamu.finance_tracker.repository;

import com.kamu.finance_tracker.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByUserIsNullOrUserId(Long userId);
}
