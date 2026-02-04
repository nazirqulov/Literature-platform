package uz.literature.platform.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import uz.literature.platform.payload.response.UserResponse;
import uz.literature.platform.entity.User;
import uz.literature.platform.exception.BadRequestException;
import uz.literature.platform.exception.ResourceNotFoundException;
import uz.literature.platform.exception.UnauthorizedException;
import uz.literature.platform.repository.UserRepository;
import uz.literature.platform.service.interfaces.UserService;
import uz.literature.platform.util.FileUploadUtil;

@Service
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ModelMapper modelMapper;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private FileUploadUtil fileUploadUtil;
    
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
        
        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }
        
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
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
        
        String fileName = fileUploadUtil.saveFile(file, "profiles");
        user.setProfileImage(fileName);
        
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
}
