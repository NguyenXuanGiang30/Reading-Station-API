package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.dto.auth.AuthResponse;
import org.example.dto.auth.LoginRequest;
import org.example.dto.auth.RegisterRequest;
import org.example.security.UserPrincipal;
import org.example.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "API xác thực người dùng")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @Operation(summary = "Đăng ký tài khoản mới")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    @Operation(summary = "Đăng nhập")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/change-password")
    @Operation(summary = "Đổi mật khẩu")
    public ResponseEntity<Map<String, String>> changePassword(
            @RequestBody Map<String, String> body,
            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal user) {
        String currentPassword = body.get("currentPassword");
        String newPassword = body.get("newPassword");
        authService.changePassword(user.getId(), currentPassword, newPassword);
        return ResponseEntity.ok(Map.of("message", "Đổi mật khẩu thành công"));
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Quên mật khẩu - Gửi email reset")
    public ResponseEntity<Map<String, String>> forgotPassword(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        authService.forgotPassword(email);
        return ResponseEntity.ok(Map.of("message", "Đã gửi email hướng dẫn đặt lại mật khẩu"));
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Đặt lại mật khẩu với token")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String resetToken = body.get("resetToken");
        String newPassword = body.get("newPassword");
        authService.resetPassword(email, resetToken, newPassword);
        return ResponseEntity.ok(Map.of("message", "Đặt lại mật khẩu thành công"));
    }
}
