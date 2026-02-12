package uz.literature.platform.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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


    //    @GetMapping("/me/profile-image")
//    public ResponseEntity<byte[]> getProfileImage(@AuthenticationPrincipal User user) {
//        byte[] image = userService.getProfileImageBytes(user);
//
//        return ResponseEntity.ok()
//                .contentType(MediaType.IMAGE_JPEG) // yoki PNG
//                .body(image);
//    }
//    @GetMapping(value = "/me/profile-image")
//    public ResponseEntity<byte[]> getProfileImage() {
//
//        byte[] imageBytes = userService.getProfileImageBytes();
//
//        if (imageBytes == null) {
//            return ResponseEntity.notFound().build();
//        }
//
//        return ResponseEntity.ok()
//                .contentType(userService.getProfileImageContentType())
//                .body(imageBytes);
//    }
    @GetMapping("/me/profile-image-url")
    public ResponseEntity<String> getProfileImageUrl() {
        User user = getCurrentUserEntity();
        String filename = user.getUserProfile().getProfileImage(); // masalan: 2346170a-b2db-4f65-a69d-17dec83ce281.png

        if (filename == null || filename.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        String url = "/profiles/" + filename; // static path orqali
        return ResponseEntity.ok(url);
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
}
