package uz.literature.platform.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    
    private String token;
    private String tokenType = "Bearer";
    private UserResponse user;
    
    public AuthResponse(String token) {
        this.token = token;
    }
}
