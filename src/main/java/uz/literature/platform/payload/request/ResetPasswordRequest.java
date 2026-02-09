package uz.literature.platform.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResetPasswordRequest {
    
    @NotBlank(message = "Token kiritilishi shart")
    private String token;

    @NotBlank(message = "Joriy parol kiritilishi shart!")
    private String currentPassword;
    
    @NotBlank(message = "Yangi parol kiritilishi shart")
    @Size(min = 6, message = "Parol kamida 6 ta belgidan iborat bo'lishi kerak")
    private String newPassword;
}
