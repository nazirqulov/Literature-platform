package uz.literature.platform.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.literature.platform.entity.User;
import uz.literature.platform.exception.ResourceNotFoundException;
import uz.literature.platform.exception.UnauthorizedException;
import uz.literature.platform.payload.response.UserResponse;
import uz.literature.platform.repository.UserRepository;
import uz.literature.platform.service.impl.UserServiceImpl;
import uz.literature.platform.service.interfaces.UserService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class UserController {


    private final UserService userService;
    private final UserServiceImpl userServiceImpl;
    private final UserRepository userRepository;

    @Value("${app.upload.dir:uploads/profiles}")
    private String uploadDir;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser() {
        UserResponse user = userService.getCurrentUser();
        return ResponseEntity.ok(user);
    }

    @PutMapping("/update-profile")
    public ResponseEntity<UserResponse> updateProfile(@RequestBody UserResponse request) {
        UserResponse user = userService.updateProfile(request);
        return ResponseEntity.ok(user);
    }

    @PostMapping(value = "/me/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserResponse> uploadProfileImage(@RequestParam MultipartFile file) {
        UserResponse user = userService.uploadProfileImage(file);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/me/change-password")
    public ResponseEntity<Map<String, String>> changePassword(
            @RequestParam String oldPassword,
            @RequestParam String newPassword) {
        userService.changePassword(oldPassword, newPassword);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Parol muvaffaqiyatli o'zgartirildi");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/me/profile-image-url")
    public ResponseEntity<String> getProfileImageUrl() {

        User user = getCurrentUserEntity();

        String filename = user.getUserProfile().getProfileImage();

        if (filename == null || filename.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        String substring = filename.substring(9);


        return ResponseEntity.ok(substring);
    }

    public User getCurrentUserEntity() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("Foydalanuvchi autentifikatsiya qilinmagan");
        }

        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Foydalanuvchi topilmadi"));
    }

    @GetMapping("/profiles/{filename}")
    public ResponseEntity<?> getImage(@PathVariable String filename) throws IOException {
        Path filePath = Paths.get(uploadDir).resolve(filename).normalize();
        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists() || !resource.isReadable()) {
            return ResponseEntity.notFound().build();
        }

        String contentType = Files.probeContentType(filePath);
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }
}
