package uz.literature.platform.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by: Barkamol
 * DateTime: 2/14/2026 12:50 PM
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RecaptchaResponse {
    private boolean success;
    private double score;
    private String action;
    private String hostname;
    private String challenge_ts;
}
