package com.kamu.finance_tracker.service;

import com.kamu.finance_tracker.dto.BudgetRequest;
import com.kamu.finance_tracker.dto.BudgetResponse;
import com.kamu.finance_tracker.dto.BudgetStatusResponse;
import com.kamu.finance_tracker.entity.Budget;
import com.kamu.finance_tracker.entity.Category;
import com.kamu.finance_tracker.entity.TransactionType;
import com.kamu.finance_tracker.entity.User;
import com.kamu.finance_tracker.repository.BudgetRepository;
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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BudgetServiceTest {

    @Mock
    private BudgetRepository budgetRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private BudgetService budgetService;

    private User mockUser;
    private Category mockCategory;
    private Budget mockBudget;
    private BudgetRequest mockRequest;

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
                .name("Makan")
                .type(TransactionType.EXPENSE)
                .build();

        mockRequest = new BudgetRequest();
        mockRequest.setCategoryId(1L);
        mockRequest.setAmount(new BigDecimal("1000000"));
        mockRequest.setMonth(6);
        mockRequest.setYear(2025);

        mockBudget = Budget.builder()
                .id(1L)
                .user(mockUser)
                .category(mockCategory)
                .amount(new BigDecimal("1000000"))
                .month(6)
                .year(2025)
                .build();
    }

    // ==================== CREATE ====================

    @Test
    void create_shouldReturnBudgetResponse_whenValidRequest() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(mockCategory));
        when(budgetRepository.existsByUserIdAndCategoryIdAndMonthAndYear(1L, 1L, 6, 2025))
                .thenReturn(false);
        when(budgetRepository.save(any(Budget.class))).thenReturn(mockBudget);

        // Act
        BudgetResponse response = budgetService.create(1L, mockRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getCategoryName()).isEqualTo("Makan");
        assertThat(response.getAmount()).isEqualByComparingTo(new BigDecimal("1000000"));
        verify(budgetRepository, times(1)).save(any(Budget.class));
    }

    @Test
    void create_shouldThrowException_whenCategoryNotFound() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> budgetService.create(1L, mockRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Category tidak ditemukan");

        verify(budgetRepository, never()).save(any());
    }

    @Test
    void create_shouldThrowException_whenBudgetAlreadyExists() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(mockCategory));
        when(budgetRepository.existsByUserIdAndCategoryIdAndMonthAndYear(1L, 1L, 6, 2025))
                .thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> budgetService.create(1L, mockRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Budget sudah ada untuk kategori dan bulan ini");

        verify(budgetRepository, never()).save(any());
    }

    // ==================== UPDATE ====================

    @Test
    void update_shouldReturnUpdatedBudget_whenValidRequest() {
        // Arrange
        when(budgetRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(mockBudget));
        when(budgetRepository.save(any(Budget.class))).thenReturn(mockBudget);

        // Act
        BudgetResponse response = budgetService.update(1L, 1L, new BigDecimal("1500000"));

        // Assert
        assertThat(response).isNotNull();
        verify(budgetRepository, times(1)).save(any(Budget.class));
    }

    @Test
    void update_shouldThrowException_whenBudgetNotFound() {
        // Arrange
        when(budgetRepository.findByIdAndUserId(9L, 9L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> budgetService.update(9L, 9L, new BigDecimal("1500000")))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Budget tidak ditemukan");

        verify(budgetRepository, never()).save(any());
    }

    // ==================== DELETE ====================

    @Test
    void delete_shouldDeleteBudget_whenValidRequest() {
        // Arrange
        when(budgetRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(mockBudget));

        // Act
        budgetService.delete(1L, 1L);

        // Assert
        verify(budgetRepository, times(1)).delete(mockBudget);
    }

    @Test
    void delete_shouldThrowException_whenBudgetNotFound() {
        // Arrange
        when(budgetRepository.findByIdAndUserId(99L, 1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> budgetService.delete(1L, 99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Budget tidak ditemukan");

        verify(budgetRepository, never()).delete(any());
    }

    // ==================== CHECK BUDGET ====================

    @Test
    void checkBudget_shouldReturnOnTrack_whenSpentBelow80Percent() {
        // Arrange
        when(budgetRepository.findByUserIdAndMonthAndYear(1L, 6, 2025))
                .thenReturn(List.of(mockBudget));
        when(transactionRepository.sumExpenseByMonthAndCategory(1L, 1L, 6, 2025))
                .thenReturn(new BigDecimal("700000")); // 70% dari 1.000.000

        // Act
        List<BudgetStatusResponse> result = budgetService.checkBudget(1L, 6, 2025);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo("ON_TRACK");
        assertThat(result.get(0).getPercentage()).isEqualTo(70.0);
    }

    @Test
    void checkBudget_shouldReturnOverBudget_whenSpentAbove100Percent() {
        // Arrange
        when(budgetRepository.findByUserIdAndMonthAndYear(1L, 6, 2025))
                .thenReturn(List.of(mockBudget));
        when(transactionRepository.sumExpenseByMonthAndCategory(1L, 1L, 6, 2025))
                .thenReturn(new BigDecimal("1200000")); // 120% dari 1.000.000

        // Act
        List<BudgetStatusResponse> result = budgetService.checkBudget(1L, 6, 2025);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo("OVER_BUDGET");
        assertThat(result.get(0).getRemaining()).isEqualByComparingTo(new BigDecimal("-200000"));
    }

    @Test
    void checkBudget_shouldReturnWarning_whenSpentBetween80And100Percent() {
        // Arrange
        when(budgetRepository.findByUserIdAndMonthAndYear(1L, 6, 2025))
                .thenReturn(List.of(mockBudget));
        when(transactionRepository.sumExpenseByMonthAndCategory(1L, 1L, 6, 2025))
                .thenReturn(new BigDecimal("850000")); // 85% dari 1.000.000

        // Act
        List<BudgetStatusResponse> result = budgetService.checkBudget(1L, 6, 2025);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo("WARNING");
    }
}