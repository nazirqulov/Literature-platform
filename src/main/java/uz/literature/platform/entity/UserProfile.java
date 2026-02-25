package uz.literature.platform.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.literature.platform.entity.base.BaseLongEntity;

/**
 * Created by: Barkamol
 * DateTime: 2/4/2026 10:10 PM
 */
@Getter
@Setter
@Entity(name = "user_profiles")
@SQLDelete(sql = "UPDATE user_profiles SET deleted = true WHERE id = ?")
@SQLRestriction("deleted=false")
@FieldNameConstants
public class UserProfile extends BaseLongEntity {

    @Column(name = "full_name")
    private String fullName;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    @JsonManagedReference
    private User user;

    private String phone;

    @Column(name = "profile_image")
    private String profileImage;

}
