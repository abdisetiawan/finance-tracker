package com.kamu.finance_tracker.service;

import com.kamu.finance_tracker.dto.CategoriesResponse;
import com.kamu.finance_tracker.entity.Category;
import com.kamu.finance_tracker.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriesService {

    private final CategoryRepository categoryRepository;

    public List<CategoriesResponse> getAllCategoriesAndByUserId(Long userId) {

        return categoryRepository.findAllAndByUserId(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private CategoriesResponse toResponse(Category category) {
        return CategoriesResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .type(String.valueOf(category.getType()))
                .build();
    }
}
