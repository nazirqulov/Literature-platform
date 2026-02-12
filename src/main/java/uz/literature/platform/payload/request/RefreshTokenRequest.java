package uz.literature.platform.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by: Barkamol
 * DateTime: 2/9/2026 9:07 AM
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RefreshTokenRequest {

    private String refreshToken;
}
