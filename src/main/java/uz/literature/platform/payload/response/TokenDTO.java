package uz.literature.platform.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TokenDTO {

    private String accessToken;

    private String refreshToken;

    private String userRole;
}
