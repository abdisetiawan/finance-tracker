package com.kamu.finance_tracker.dto;

import com.kamu.finance_tracker.entity.TransactionType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequest {

    @NotNull(message = "Category id wajib diisi")
    private Long categoryId;

    @NotNull(message = "Amount wajib diisi")
    @Positive(message = "Amount harus lebih dari 0")
    private BigDecimal amount;

    @NotNull(message = "Type wajib diisi")
    private TransactionType type;

    private String note;

    @NotNull(message = "Tanggal wajib diisi")
    private LocalDate date;
}
