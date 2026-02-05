package uz.literature.platform.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Created by: Barkamol
 * DateTime: 2/5/2026 10:47 AM
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PendingRegistration {

    private String username;
    private String email;
    private String encodedPassword;
    private String verificationCode;
    private LocalDateTime expiryTime;
}
