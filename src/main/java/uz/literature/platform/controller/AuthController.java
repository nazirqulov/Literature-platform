package uz.literature.platform.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.literature.platform.payload.request.*;
import uz.literature.platform.payload.response.AuthResponse;
import uz.literature.platform.payload.response.TokenDTO;
import uz.literature.platform.security.JwtTokenProvider;
import uz.literature.platform.service.interfaces.AuthService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class AuthController {

    private final AuthService authService;

    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        TokenDTO response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@Valid @RequestBody RegisterRequest request) {
        String message = authService.register(request);

        Map<String, String> response = new HashMap<>();
        response.put("message", message);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@Valid @RequestBody uz.literature.platform.payload.request.VerifyEmailRequest request) {
        TokenDTO response = authService.verifyEmail(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {

        authService.forgotPassword(request);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Parolni tiklash tartibi emailingizga yuborildi");

        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Parol muvaffaqiyatli yangilandi");

        return ResponseEntity.ok(response);
    }

    @PostMapping("/auth/refresh")
    public AuthResponse refresh(@RequestBody RefreshTokenRequest request) {
        return jwtTokenProvider.refreshToken(request);
    }
}
