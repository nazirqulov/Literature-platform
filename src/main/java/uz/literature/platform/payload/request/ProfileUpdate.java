package uz.literature.platform.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by: Barkamol
 * DateTime: 2/5/2026 9:15 AM
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProfileUpdate {

    private String email;

    private String fullName;

    private String phoneNumber;
}
