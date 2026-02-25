package uz.literature.platform.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.literature.platform.entity.User;
import uz.literature.platform.payload.request.UserRequestDTO;
import uz.literature.platform.payload.response.UserResponse;
import uz.literature.platform.repository.UserRepository;
import uz.literature.platform.service.impl.UserServiceImpl;
import uz.literature.platform.service.interfaces.UserService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static uz.literature.platform.service.impl.UserServiceImpl.getUser;

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


    @GetMapping("/user/get-all")
    public ResponseEntity<List<UserResponse>> getAllUsers(@RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "10") int size,
                                                          @RequestParam(required = false) User.Role role,
                                                          @RequestParam(required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate fromDate,
                                                          @RequestParam(required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate toDate) {
        List<UserResponse> users = userServiceImpl.getAllUsers(page, size, role, fromDate, toDate);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/user/get-all-role")
    public ResponseEntity<List<String>> getAllRoles() {
        List<String> roles = userServiceImpl.getAllRoles();
        return ResponseEntity.ok(roles);
    }

    @PostMapping("/user/create")
    public ResponseEntity<UserResponse> createUser(@RequestBody UserRequestDTO request) {
        UserResponse user = userService.createUser(request);
        return ResponseEntity.ok(user);
    }

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

    @PutMapping("/update/{id}")
    public ResponseEntity<UserResponse> update(@PathVariable Long id, @RequestBody UserResponse request) {
        UserResponse user = userService.updateProfile(request, id);
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
        return getUser(userRepository);
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

    @DeleteMapping("/user/delete/{id}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Foydalanuvchi o'chirildi");

        return ResponseEntity.ok(response);
    }
}
