package uz.literature.platform.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Created by: Barkamol
 * DateTime: 2/23/2026 10:55 AM
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserRequestDTO {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String phone;
    private String profileImage;
    private String role;
    private Boolean isActive;
    private Boolean emailVerified;
    private LocalDateTime createdAt;
    private String password;
}
