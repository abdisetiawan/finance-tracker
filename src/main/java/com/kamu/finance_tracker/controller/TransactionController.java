package com.kamu.finance_tracker.controller;

import com.kamu.finance_tracker.dto.SummaryResponse;
import com.kamu.finance_tracker.dto.TransactionRequest;
import com.kamu.finance_tracker.dto.TransactionResponse;
import com.kamu.finance_tracker.entity.TransactionType;
import com.kamu.finance_tracker.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
import com.kamu.finance_tracker.service.TransactionService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<TransactionResponse> create(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody TransactionRequest request) {
        Long userId = getUserId(userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(transactionService.create(userId, request));
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponse>> getAll(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserId(userDetails.getUsername());
        return ResponseEntity.ok(transactionService.getAll(userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponse> update(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @Valid @RequestBody TransactionRequest request) {
        Long userId = getUserId(userDetails.getUsername());
        return ResponseEntity.ok(transactionService.update(userId, id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        Long userId = getUserId(userDetails.getUsername());
        transactionService.delete(userId, id);
        return ResponseEntity.noContent().build();
    }

    private Long getUserId(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"))
                .getId();
    }

    @GetMapping("/paginated")
    public ResponseEntity<Page<TransactionResponse>> getAllPaginated(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = getUserId(userDetails.getUsername());
        return ResponseEntity.ok(transactionService.getAllPaginated(userId, page, size));
    }

    @GetMapping("/filter/category")
    public ResponseEntity<Page<TransactionResponse>> filterByCategory(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = getUserId(userDetails.getUsername());
        return ResponseEntity.ok(transactionService.filterByCategory(userId, categoryId, page, size));
    }

    @GetMapping("/filter/type")
    public ResponseEntity<Page<TransactionResponse>> filterByType(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam TransactionType type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = getUserId(userDetails.getUsername());
        return ResponseEntity.ok(transactionService.filterByType(userId, type, page, size));
    }

    @GetMapping("/filter/date")
    public ResponseEntity<Page<TransactionResponse>> filterByDateRange(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = getUserId(userDetails.getUsername());
        return ResponseEntity.ok(transactionService.filterByDateRange(userId, startDate, endDate, page, size));
    }

    @GetMapping("/summary")
    public ResponseEntity<SummaryResponse> getMonthlySummary(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam int month,
            @RequestParam int year) {
        Long userId = getUserId(userDetails.getUsername());
        return ResponseEntity.ok(transactionService.getMonthlySummary(userId, month, year));
    }
}
