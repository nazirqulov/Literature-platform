package uz.literature.platform.service.interfaces;

import uz.literature.platform.payload.request.ForgotPasswordRequest;
import uz.literature.platform.payload.request.LoginRequest;
import uz.literature.platform.payload.request.RegisterRequest;
import uz.literature.platform.payload.request.ResetPasswordRequest;
import uz.literature.platform.payload.request.VerifyEmailRequest;
import uz.literature.platform.payload.response.AuthResponse;
import uz.literature.platform.payload.response.TokenDTO;

public interface AuthService {
    
    TokenDTO login(LoginRequest request);
    
    String register(RegisterRequest request);
    
    TokenDTO verifyEmail(VerifyEmailRequest request);
    
    void forgotPassword(ForgotPasswordRequest request);
    
    void resetPassword(ResetPasswordRequest request);

    Object loadUserByUsername(String email);
}
