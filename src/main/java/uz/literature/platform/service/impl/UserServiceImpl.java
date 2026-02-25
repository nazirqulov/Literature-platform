package uz.literature.platform.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import uz.literature.platform.payload.request.UserRequestDTO;
import uz.literature.platform.payload.response.UserResponse;
import uz.literature.platform.repository.UserProfileRepository;
import uz.literature.platform.repository.UserRepository;
import uz.literature.platform.service.interfaces.UserService;
import uz.literature.platform.util.FileUploadUtil;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserProfileRepository userProfileRepository;

    private final ModelMapper modelMapper;

    private final PasswordEncoder passwordEncoder;

    private final FileUploadUtil fileUploadUtil;

    @Override
    public UserResponse getCurrentUser() {
        User user = getCurrentUserEntity();
        UserResponse response = modelMapper.map(user, UserResponse.class);
        UserProfile userProfile = user.getUserProfile();
        response.setPhone(userProfile.getPhone());
        response.setRole(user.getRole().name());
        response.setFullName(userProfile.getFullName());


        response.setProfileImage(userProfile.getProfileImage());
        return response;
    }


    private User getCurrentUserEntitys() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return (User) userRepository.findByUsernameAndIsActiveTrueAndDeletedFalse(username)
                .orElseThrow(() -> new RuntimeException("Foydalanuvchi topilmadi"));
    }

    @Override
    public User getCurrentUserEntity() {
        return getUser(userRepository);
    }

    public static User getUser(UserRepository userRepository) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("Foydalanuvchi autentifikatsiya qilinmagan");
        }

        String username = authentication.getName();
        return (User) userRepository.findByUsernameAndIsActiveTrueAndDeletedFalse(username)
                .orElseThrow(() -> new ResourceNotFoundException("Foydalanuvchi topilmadi"));
    }


    @Override
    @Transactional
    public UserResponse updateProfile(UserResponse request) {

        User user = getCurrentUserEntity();
        return getUserResponse(request, user);
    }

    @Override
    public UserResponse updateProfile(UserResponse request, Long id) {

        Optional<User> byId = userRepository.findByIdAndIsActiveTrueAndDeletedFalse(id);

        if (byId.isEmpty()) {
            throw new ResourceNotFoundException("User not found with id: " + id);

        }

        User user = byId.get();
        return getUserResponse(request, user);
    }

    @NotNull
    private UserResponse getUserResponse(UserResponse request, User user) {
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

        if (request.getRole() != null)
            profile.getUser().setRole(User.Role.valueOf(request.getRole()));


        user.setUserProfile(profile);

        User updatedUser = userRepository.save(user);

        return getUserResponse(updatedUser, profile);
    }


//    @NotNull
//    private static UserResponse getUserResponse(User updatedUser, UserProfile profile) {
//        UserResponse response = new UserResponse();
//        response.setId(updatedUser.getId());
//        response.setUsername(updatedUser.getUsername());
//        response.setEmail(updatedUser.getEmail());
//        response.setFullName(profile.getFullName());
//        response.setPhone(profile.getPhone());
//        response.setRole(updatedUser.getRole().name());
//        response.setIsActive(updatedUser.getIsActive());
//        response.setEmailVerified(updatedUser.getEmailVerified());
//        response.setCreatedAt(updatedUser.getCreatedAt());
//        response.setProfileImage(profile.getProfileImage());
//        return response;
//    }

    @NotNull
    private static UserResponse getUserResponse(User user, UserProfile profile) {

        UserResponse response = new UserResponse();

        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());

        if (profile != null) {
            response.setFullName(profile.getFullName());
            response.setPhone(profile.getPhone());
            response.setProfileImage(profile.getProfileImage());
        }

        response.setRole(user.getRole() != null ? user.getRole().name() : null);
        response.setIsActive(user.getIsActive());
        response.setEmailVerified(user.getEmailVerified());
        response.setCreatedAt(user.getCreatedAt());

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
        return (User) userRepository.findByUsernameAndIsActiveTrueAndDeletedFalse(username)
                .orElseThrow(() -> {
                    log.warn("User not found with username: {}", username);
                    return new EntityNotFoundException("User not found with username: " + username);
                });
    }


    @Override
    public List<UserResponse> getAllUsers(int page, int size, User.Role role, LocalDate fromDate, LocalDate toDate) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> usersPage;

        if (role != null && fromDate != null && toDate != null) {
            usersPage = userRepository
                    .findAllByRoleAndCreatedAtBetweenAndIsActiveTrueAndDeletedFalse(
                            role,
                            fromDate.atStartOfDay(),
                            toDate.atStartOfDay().plusDays(1),
                            pageable
                    );

        } else if (role != null) {
            usersPage = userRepository.findAllByRoleAndIsActiveTrueAndDeletedFalse(role, pageable);

        } else if (fromDate != null && toDate != null) {
            usersPage = userRepository
                    .findAllByCreatedAtBetween(
                            fromDate.atStartOfDay(),
                            toDate.atStartOfDay().plusDays(1),
                            pageable
                    );

        } else {
            usersPage = userRepository.findAll(pageable);
        }

        return usersPage.stream()
                .map(user -> getUserResponse(user, user.getUserProfile()))
                .toList();
    }

    @Override
    public void deleteUser(Long id) {
        Optional<User> byId = userRepository.findByIdAndIsActiveTrueAndDeletedFalse(id);
        if (byId.isEmpty()) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }

        User user = byId.get();
        user.setDeleted(true);
        user.setIsActive(false);
        userRepository.save(user);
    }

    @Override
    public UserResponse createUser(UserRequestDTO request) {

        if (userRepository.existsByUsernameAndIsActiveTrueAndDeletedFalse(request.getUsername())) {
            throw new BadRequestException("Bu username allaqachon mavjud");
        }
        if (userRepository.existsByEmailAndIsActiveTrueAndDeletedFalse(request.getEmail())) {
            throw new BadRequestException("Bu email allaqachon ro'yxatdan o'tgan");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole() != null ? User.Role.valueOf(request.getRole()) : User.Role.USER);
        user.setIsActive(true);
        user.setEmailVerified(false);
        user.setEmail(request.getEmail());

        UserProfile userProfile = new UserProfile();
        userProfile.setFullName(request.getFullName());
        userProfile.setPhone(request.getPhone());
        userProfile.setProfileImage(request.getProfileImage());
        userProfile.setUser(user);

        user.setUserProfile(userProfile);

        User savedUser = userRepository.save(user);

        userProfileRepository.save(userProfile);

        return modelMapper.map(savedUser, UserResponse.class);
    }

    public List<String> getAllRoles() {
        return Stream.of(User.Role.values())
                .map(Enum::name)
                .toList();
    }
}
