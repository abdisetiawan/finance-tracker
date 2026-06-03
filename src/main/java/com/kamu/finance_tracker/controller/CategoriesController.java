package com.kamu.finance_tracker.controller;

import com.kamu.finance_tracker.dto.CategoriesResponse;
import com.kamu.finance_tracker.repository.UserRepository;
import com.kamu.finance_tracker.service.AuthService;
import com.kamu.finance_tracker.service.CategoriesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoriesController {

    private final AuthService authService;
    private final CategoriesService categoriesService;

    @GetMapping
    public ResponseEntity<List<CategoriesResponse>> getAll(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = authService.getUserId(userDetails.getUsername());

        return ResponseEntity.ok(categoriesService.getAllCategoriesAndByUserId(userId));
    }
}
