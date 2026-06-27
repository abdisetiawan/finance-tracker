package com.kamu.finance_tracker.service;

import com.kamu.finance_tracker.dto.TransactionRequest;
import com.kamu.finance_tracker.dto.TransactionResponse;
import com.kamu.finance_tracker.entity.Category;
import com.kamu.finance_tracker.entity.Transaction;
import com.kamu.finance_tracker.entity.TransactionType;
import com.kamu.finance_tracker.entity.User;
import com.kamu.finance_tracker.repository.CategoryRepository;
import com.kamu.finance_tracker.repository.TransactionRepository;
import com.kamu.finance_tracker.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TransactionService transactionService;

    private User mockUser;
    private Category mockCategory;
    private Transaction mockTransaction;
    private TransactionRequest mockRequest;

    @BeforeEach
    void setUp() {
        mockUser = User.builder()
                .id(1L)
                .email("budi@gmail.com")
                .fullName("Budi Santoso")
                .passwordHash("hashed")
                .build();

        mockCategory = Category.builder()
                .id(1L)
                .name("Gaji")
                .type(TransactionType.INCOME)
                .build();

        mockRequest = new TransactionRequest();
        mockRequest.setCategoryId(1L);
        mockRequest.setAmount(new BigDecimal("5000000"));
        mockRequest.setType(TransactionType.INCOME);
        mockRequest.setNote("Gaji bulan ini");
        mockRequest.setDate(LocalDate.of(2025, 1, 1));

        mockTransaction = Transaction.builder()
                .id(1L)
                .user(mockUser)
                .category(mockCategory)
                .amount(new BigDecimal("5000000"))
                .type(TransactionType.INCOME)
                .note("Gaji bulan ini")
                .date(LocalDate.of(2025, 1, 1))
                .build();
    }

    @Test
    void create_shouldReturnTransactionResponse_whenValidRequest() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(mockCategory));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(mockTransaction);

        // Act
        TransactionResponse response = transactionService.create(1L, mockRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getAmount()).isEqualByComparingTo(new BigDecimal("5000000"));
        assertThat(response.getType()).isEqualTo(TransactionType.INCOME);
        assertThat(response.getCategoryName()).isEqualTo("Gaji");

        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void create_shouldThrowException_whenUserNotFound() {
        // Arrange
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> transactionService.create(99L, mockRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User tidak ditemukan");

        verify(transactionRepository, never()).save(any());
    }

    @Test
    void create_shouldThrowException_whenCategoryNotFound() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> transactionService.create(1L, mockRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Category tidak ditemukan");

        verify(transactionRepository, never()).save(any());
    }

    @Test
    void delete_shouldDeleteTransaction_whenOwnerRequest() {
        // Arrange
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(mockTransaction));

        // Act
        transactionService.delete(1L, 1L);

        // Assert
        verify(transactionRepository, times(1)).delete(mockTransaction);
    }

    @Test
    void delete_shouldThrowException_whenNotOwner() {
        // Arrange
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(mockTransaction));

        // Act & Assert — userId 99 bukan pemilik transaction ini
        assertThatThrownBy(() -> transactionService.delete(99L, 1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Transaksi bukan milik user ini");

        verify(transactionRepository, never()).delete(any());
    }
}