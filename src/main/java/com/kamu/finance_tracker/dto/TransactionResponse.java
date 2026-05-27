package com.kamu.finance_tracker.dto;

import com.kamu.finance_tracker.entity.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionResponse {
    private Long id;
    private Long categoryId;
    private String categoryName;
    private TransactionType type;
    private BigDecimal amount;
    private String note;
    private LocalDate date;
    private LocalDateTime createdAt;
}
