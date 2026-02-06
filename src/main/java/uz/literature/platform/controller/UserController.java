package uz.literature.platform.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.literature.platform.payload.response.UserResponse;
import uz.literature.platform.service.interfaces.UserService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class UserController {
    

    private final UserService userService;
    
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
    
    @PostMapping(value = "/me/profile-image",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
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
}
