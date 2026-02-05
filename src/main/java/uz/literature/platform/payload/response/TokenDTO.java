package uz.literature.platform.payload.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TokenDTO {

    @Builder.Default
    private String tokenType = "Bearer";

    private String accessToken;

    private String refreshToken;

    @JsonProperty("expires_in")
    private Long expiresIn;

    private Set<String> authorities;

    private String username;
}
