package com.kamu.finance_tracker.repository;

import com.kamu.finance_tracker.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByUserIsNullOrUserId(Long userId);

    @Query(value = """
    SELECT *
    FROM categories c
    WHERE c.user_id IS NULL
       OR c.user_id = :userId
    """, nativeQuery = true)
    List<Category> findAllAndByUserId(@Param("userId") Long userId);
}
