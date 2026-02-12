package uz.literature.platform.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import uz.literature.platform.entity.User;
import uz.literature.platform.entity.UserProfile;
import uz.literature.platform.exception.BadRequestException;
import uz.literature.platform.exception.ResourceNotFoundException;
import uz.literature.platform.exception.UnauthorizedException;
import uz.literature.platform.payload.response.UserResponse;
import uz.literature.platform.repository.UserRepository;
import uz.literature.platform.service.interfaces.UserService;
import uz.literature.platform.util.FileUploadUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final ModelMapper modelMapper;

    private final PasswordEncoder passwordEncoder;

    private final FileUploadUtil fileUploadUtil;

    @Override
    public UserResponse getCurrentUser() {
        User user = getCurrentUserEntity();
        UserResponse response = modelMapper.map(user, UserResponse.class);
        UserProfile userProfile = user.getUserProfile();
        response.setPhone(userProfile.getPhone());
        response.setEmail(userProfile.getEmail());
        response.setRole(user.getRole().name());
        response.setFullName(userProfile.getFullName());


        response.setProfileImage(userProfile.getProfileImage());
        return response;
    }

    public String getCurrentUserProfileImage() {
        User user = getCurrentUserEntitys(); // token orqali yoki SecurityContext orqali
        UserProfile profile = user.getUserProfile();
        return profile != null ? profile.getProfileImage() : null;
    }

    private User getCurrentUserEntitys() {
        // JWT orqali current user ni olish
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Foydalanuvchi topilmadi"));
    }

    @Override
    public User getCurrentUserEntity() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("Foydalanuvchi autentifikatsiya qilinmagan");
        }

        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Foydalanuvchi topilmadi"));
    }

    //    @Override
//    @Transactional
//    public UserResponse updateProfile(UserResponse request) {
//
//        User user = getCurrentUserEntity();
//
//        UserProfile userProfile = user.getUserProfile();
//
//        if (request.getFullName() != null) {
//            userProfile.setFullName(request.getFullName());
//        }
//
//        if (request.getPhone() != null) {
//            userProfile.setPhone(request.getPhone());
//        }
//
//        User updatedUser = userRepository.save(user);
//        UserResponse response = modelMapper.map(updatedUser, UserResponse.class);
//        response.setRole(updatedUser.getRole().name());
//
//        return response;
//    }
    @Override
    @Transactional
    public UserResponse updateProfile(UserResponse request) {

        User user = getCurrentUserEntity();
        UserProfile profile = user.getUserProfile();

        if (request.getUsername() != null)
            user.setUsername(request.getUsername());

        if (request.getEmail() != null)
            user.setEmail(request.getEmail());

        if (request.getFullName() != null)
            profile.setFullName(request.getFullName());

        if (request.getPhone() != null)
            profile.setPhone(request.getPhone());

        if (request.getProfileImage() != null)
            profile.setProfileImage(request.getProfileImage());

        user.setUserProfile(profile);

        User updatedUser = userRepository.save(user);

        return getUserResponse(updatedUser, profile);
    }


    @NotNull
    private static UserResponse getUserResponse(User updatedUser, UserProfile profile) {
        UserResponse response = new UserResponse();
        response.setId(updatedUser.getId());
        response.setUsername(updatedUser.getUsername());
        response.setEmail(updatedUser.getEmail());
        response.setFullName(profile.getFullName());
        response.setPhone(profile.getPhone());
        response.setRole(updatedUser.getRole().name());
        response.setIsActive(updatedUser.getIsActive());
        response.setEmailVerified(updatedUser.getEmailVerified());
        response.setCreatedAt(updatedUser.getCreatedAt());
        response.setProfileImage(profile.getProfileImage());
        return response;
    }

    @Override
    @Transactional
    public UserResponse uploadProfileImage(MultipartFile file) {

        User user = getCurrentUserEntity();

        UserProfile userProfile = user.getUserProfile();

        String fileName = fileUploadUtil.saveFile(file, "profiles");

        userProfile.setProfileImage(fileName);

        User updatedUser = userRepository.save(user);

        UserResponse response = modelMapper.map(updatedUser, UserResponse.class);

        response.setRole(updatedUser.getRole().name());

        return response;
    }

    @Override
    @Transactional
    public void changePassword(String oldPassword, String newPassword) {
        User user = getCurrentUserEntity();

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BadRequestException("Eski parol noto'g'ri");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return getUser(username);
    }

    private User getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("User not found with username: {}", username);
                    return new EntityNotFoundException("User not found with username: " + username);
                });
    }


//    @Override
//    public byte[] getProfileImageBytes(User user1) {
//
//        UserResponse user = getCurrentUser();
//
//        if (user == null || user.getId() == null) {
//            return null;
//        }
//
//        // Foydalanuvchini bazadan olish
//        User userEntity = userRepository.findById(user.getId())
//                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + user.getId()));
//
//        UserProfile userProfile = userEntity.getUserProfile();
//        if (userProfile == null) {
//            return null;
//        }
//
//        String profileImagePath = userProfile.getProfileImage();
//        if (profileImagePath == null || profileImagePath.isEmpty()) {
//            return null;
//        }
//
//        try {
//            // Faylni o‘qish va byte[] ga aylantirish
//            Path path = Paths.get(profileImagePath); // bu to‘liq path yoki rootdan relative path
//            if (!Files.exists(path)) {
//                return null; // Fayl mavjud bo‘lmasa
//            }
//            return Files.readAllBytes(path);
//        } catch (IOException e) {
//            // Hatolikni log qilishingiz mumkin
//            e.printStackTrace();
//            return null;
//        }
//    }
//
@Override
public byte[] getProfileImageBytes() {
    User currentUser = getCurrentUserEntitys();

    if (currentUser == null || currentUser.getId() == null) {
        throw new RuntimeException("User not authenticated");
    }

    User userEntity = userRepository.findById(currentUser.getId())
            .orElseThrow(() -> new EntityNotFoundException(
                    "User not found with id: " + currentUser.getId()
            ));

    UserProfile profile = userEntity.getUserProfile();
    if (profile == null || profile.getProfileImage() == null) {
        return null;
    }

    try {
        Path path = Paths.get(profile.getProfileImage());

        if (!Files.exists(path)) {
            throw new RuntimeException("Image file not found: " + path);
        }

        return Files.readAllBytes(path);

    } catch (IOException e) {
        throw new RuntimeException("Error reading image file", e);
    }
}
    @Override
    public MediaType getProfileImageContentType() {
        User currentUser = getCurrentUserEntitys();

        User userEntity = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        String imagePath = userEntity.getUserProfile().getProfileImage();
        try {
            Path path = Paths.get(imagePath);
            String contentType = Files.probeContentType(path);

            return MediaType.parseMediaType(
                    contentType != null ? contentType : MediaType.APPLICATION_OCTET_STREAM_VALUE
            );

        } catch (IOException e) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
    }

}
