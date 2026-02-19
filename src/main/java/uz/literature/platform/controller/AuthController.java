package uz.literature.platform.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import retrofit2.http.HTTP;
import uz.literature.platform.payload.request.*;
import uz.literature.platform.payload.response.AuthResponse;
import uz.literature.platform.payload.response.TokenDTO;
import uz.literature.platform.security.JwtTokenProvider;
import uz.literature.platform.service.impl.RecaptchaService;
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

    private final RecaptchaService recaptchaService;

        @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        TokenDTO response = authService.login(request);
        return ResponseEntity.ok(response);
    }
//    @PostMapping("/login")
//    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request,
//                                   HttpServletRequest httpServletRequest) {
//
//        if (request.getRecaptchaToken() == null || request.getRecaptchaToken().isBlank()) {
//            return ResponseEntity.status(403).body("Missing reCAPTCHA token");
//        }
//
//        boolean ok = recaptchaService.isValid(
//                request.getRecaptchaToken(),
//                httpServletRequest.getRemoteAddr()
//        );
//
//        if (!ok) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Bot activity detected");
//        }
//
//        TokenDTO response = authService.login(request);
//        return ResponseEntity.ok(response);
//    }

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
