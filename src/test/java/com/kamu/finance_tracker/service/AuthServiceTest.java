package com.kamu.finance_tracker.service;

import com.kamu.finance_tracker.config.JwtService;
import com.kamu.finance_tracker.dto.AuthResponse;
import com.kamu.finance_tracker.dto.LoginRequest;
import com.kamu.finance_tracker.dto.RegisterRequest;
import com.kamu.finance_tracker.entity.User;
import com.kamu.finance_tracker.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private User mockUser;
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        mockUser = User.builder()
                .id(1L)
                .email("budi@gmail.com")
                .fullName("Budi Santoso")
                .passwordHash("hashed_password")
                .createdAt(LocalDateTime.now())
                .build();

        registerRequest = new RegisterRequest();
        registerRequest.setFullName("Budi Santoso");
        registerRequest.setEmail("budi@gmail.com");
        registerRequest.setPassword("password123");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("budi@gmail.com");
        loginRequest.setPassword("password123");
    }

    @Test
    void register_shouldReturnAuthResponse_whenValidRequest() {
        // Arrange
        when(userRepository.existsByEmail("budi@gmail.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashed_password");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(jwtService.generateToken("budi@gmail.com")).thenReturn("mock.jwt.token");

        // Act
        AuthResponse response = authService.register(registerRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("mock.jwt.token");
        assertThat(response.getEmail()).isEqualTo("budi@gmail.com");
        assertThat(response.getFullName()).isEqualTo("Budi Santoso");

        verify(userRepository, times(1)).save(any(User.class));
        verify(passwordEncoder, times(1)).encode("password123");
    }

    @Test
    void register_shouldThrowException_whenEmailAlreadyExists() {
        // Arrange
        when(userRepository.existsByEmail("budi@gmail.com")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Email sudah terdaftar");

        verify(userRepository, never()).save(any());
    }

    @Test
    void login_shouldReturnAuthResponse_whenValidCredentials() {
        // Arrange
        when(userRepository.findByEmail("budi@gmail.com")).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("password123", "hashed_password")).thenReturn(true);
        when(jwtService.generateToken("budi@gmail.com")).thenReturn("mock.jwt.token");

        // Act
        AuthResponse response = authService.login(loginRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("mock.jwt.token");
        assertThat(response.getEmail()).isEqualTo("budi@gmail.com");

        verify(jwtService, times(1)).generateToken("budi@gmail.com");
    }

    @Test
    void login_shouldThrowException_whenEmailNotFound() {
        // Arrange
        when(userRepository.findByEmail("notfound@gmail.com")).thenReturn(Optional.empty());

        loginRequest.setEmail("notfound@gmail.com");

        // Act & Assert
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Email tidak ditemukan");

        verify(jwtService, never()).generateToken(anyString());
    }

    @Test
    void login_shouldThrowException_whenWrongPassword() {
        // Arrange
        when(userRepository.findByEmail("budi@gmail.com")).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("password123", "hashed_password")).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Password salah");

        verify(jwtService, never()).generateToken(anyString());
    }
}