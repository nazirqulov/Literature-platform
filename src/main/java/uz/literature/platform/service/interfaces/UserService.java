package uz.literature.platform.service.interfaces;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import uz.literature.platform.payload.request.UserRequestDTO;
import uz.literature.platform.payload.response.UserResponse;
import uz.literature.platform.entity.User;

import java.time.LocalDate;
import java.util.List;

public interface UserService {
    
    UserResponse getCurrentUser();
    
    User getCurrentUserEntity();
    
    UserResponse updateProfile(UserResponse request);
    
    UserResponse updateProfile(UserResponse request, Long id);

    UserResponse uploadProfileImage(MultipartFile file);
    
    void changePassword(String oldPassword, String newPassword);

    Object loadUserByUsername(String username);

    List<UserResponse> getAllUsers(int page, int size, User.Role role, LocalDate fromDate, LocalDate toDate);

    void deleteUser(Long id);

    UserResponse createUser(UserRequestDTO request);
}
