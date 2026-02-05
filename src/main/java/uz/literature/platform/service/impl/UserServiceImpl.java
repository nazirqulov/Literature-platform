package uz.literature.platform.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
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
        response.setRole(user.getRole().name());
        return response;
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

    @Override
    @Transactional
    public UserResponse updateProfile(UserResponse request) {
        User user = getCurrentUserEntity();

        UserProfile userProfile = user.getUserProfile();

        if (request.getFullName() != null) {
            userProfile.setFullName(request.getFullName());
        }

        if (request.getPhone() != null) {
            userProfile.setPhone(request.getPhone());
        }

        User updatedUser = userRepository.save(user);
        UserResponse response = modelMapper.map(updatedUser, UserResponse.class);
        response.setRole(updatedUser.getRole().name());

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
}
