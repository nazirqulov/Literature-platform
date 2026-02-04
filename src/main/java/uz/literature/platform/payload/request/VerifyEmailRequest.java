package uz.literature.platform.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VerifyEmailRequest {
    @NotBlank(message = "Email kiritilishi shart")
    @Email(message = "Noto'g'ri email format")
    private String email;

    @NotBlank(message = "Kod kiritilishi shart")
    private String code;
}
