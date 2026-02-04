package uz.literature.platform.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    
    @NotBlank(message = "Username yoki email kiritilishi shart")
    private String usernameOrEmail;
    
    @NotBlank(message = "Parol kiritilishi shart")
    private String password;
}
