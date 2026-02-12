package uz.literature.platform.service.interfaces;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import uz.literature.platform.payload.response.UserResponse;
import uz.literature.platform.entity.User;

public interface UserService {
    
    UserResponse getCurrentUser();
    
    User getCurrentUserEntity();
    
    UserResponse updateProfile(UserResponse request);
    
    UserResponse uploadProfileImage(MultipartFile file);
    
    void changePassword(String oldPassword, String newPassword);

    Object loadUserByUsername(String username);

    byte[] getProfileImageBytes();

    MediaType getProfileImageContentType();

}
