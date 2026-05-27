package com.kamu.finance_tracker.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "Nama wajib diisi")
    private String fullName;

    @Email(message = "Format email tidak valid")
    @NotBlank(message = "Email wajib diisi")
    private String email;

    @NotBlank(message = "Password wajib diisi")
    private String password;
}