package com.kamu.finance_tracker.controller;

import com.kamu.finance_tracker.dto.BudgetRequest;
import com.kamu.finance_tracker.dto.BudgetResponse;
import com.kamu.finance_tracker.dto.BudgetStatusResponse;
import com.kamu.finance_tracker.dto.TransactionRequest;
import com.kamu.finance_tracker.service.AuthService;
import com.kamu.finance_tracker.service.BudgetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/budget")
@RequiredArgsConstructor
public class BudgetController {

    private final AuthService authService;
    private final BudgetService budgetService;

    @PostMapping
    public ResponseEntity<BudgetResponse> create(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody BudgetRequest request) {
        Long userId = authService.getUserId(userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(budgetService.create(userId, request));
    }

    @GetMapping
    public ResponseEntity<List<BudgetResponse>> getAll(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = authService.getUserId(userDetails.getUsername());
        return ResponseEntity.ok(budgetService.getAll(userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BudgetResponse> update(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @RequestParam BigDecimal amount) {
        Long userId = authService.getUserId(userDetails.getUsername());
        return ResponseEntity.ok(budgetService.update(id,userId, amount));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        Long userId = authService.getUserId(userDetails.getUsername());
        budgetService.delete(userId, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/check")
    public ResponseEntity<List<BudgetStatusResponse>> checkBudgetStatus(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam Integer month,
            @RequestParam Integer year) {
        Long userId = authService.getUserId(userDetails.getUsername());
        return ResponseEntity.ok(budgetService.checkBudget(userId, month, year));
    }
}
