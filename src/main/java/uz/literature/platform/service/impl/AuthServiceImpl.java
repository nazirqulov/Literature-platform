/// /package uz.literature.platform.service.impl;
/// /
/// /import jakarta.persistence.EntityNotFoundException;
/// /import lombok.extern.slf4j.Slf4j;
/// /import org.modelmapper.ModelMapper;
/// /import org.springframework.beans.factory.annotation.Autowired;
/// /import org.springframework.mail.SimpleMailMessage;
/// /import org.springframework.mail.javamail.JavaMailSender;
/// /import org.springframework.security.authentication.BadCredentialsException;
/// /import org.springframework.security.core.GrantedAuthority;
/// /import org.springframework.security.crypto.password.PasswordEncoder;
/// /import org.springframework.stereotype.Service;
/// /import org.springframework.transaction.annotation.Transactional;
/// /import uz.literature.platform.entity.User;
/// /import uz.literature.platform.entity.UserProfile;
/// /import uz.literature.platform.exception.BadRequestException;
/// /import uz.literature.platform.exception.ResourceNotFoundException;
/// /import uz.literature.platform.payload.request.*;
/// /import uz.literature.platform.payload.response.TokenDTO;
/// /import uz.literature.platform.payload.response.UserResponse;
/// /import uz.literature.platform.repository.UserProfileRepository;
/// /import uz.literature.platform.repository.UserRepository;
/// /import uz.literature.platform.security.JwtTokenProvider;
/// /import uz.literature.platform.service.JwtTokenProperties;
/// /import uz.literature.platform.service.interfaces.AuthService;
/// /
/// /import java.time.LocalDateTime;
/// /import java.util.Random;
/// /import java.util.UUID;
/// /import java.util.stream.Collectors;
/// /
/// /@Service
/// /@Slf4j
/// /public class AuthServiceImpl implements AuthService {
/// /
/// /    private final UserRepository userRepository;
/// /    private final PasswordEncoder passwordEncoder;
/// /    private final JwtTokenProvider tokenProvider;
/// /    private final ModelMapper modelMapper;
/// /    private final JavaMailSender mailSender;
/// /
/// /    private final JwtTokenProperties jwtTokenProvider;
/// /    private final UserProfileRepository userProfileRepository;
/// /
/// /
/// /    @Autowired
/// /    public AuthServiceImpl(UserRepository userRepository,
/// /                           PasswordEncoder passwordEncoder,
/// /                           JwtTokenProvider tokenProvider,
/// /                           ModelMapper modelMapper,
/// /                           JavaMailSender mailSender,
/// /                           JwtTokenProperties jwtTokenProvider, UserProfileRepository userProfileRepository) {
/// /        this.userRepository = userRepository;
/// /        this.passwordEncoder = passwordEncoder;
/// /        this.tokenProvider = tokenProvider;
/// /        this.modelMapper = modelMapper;
/// /        this.mailSender = mailSender;
/// /        this.jwtTokenProvider = jwtTokenProvider;
/// /        this.userProfileRepository = userProfileRepository;
/// /    }
/// /
/// /    @Override
/// /    @Transactional
/// /    public TokenDTO login(LoginRequest request) {
/// /        String password = request.getPassword();
/// /        String username = request.getUsernameOrEmail();
/// /
/// /        User user = userRepository.findByUsername(username)
/// /                .orElseThrow(() -> new EntityNotFoundException("User not found with username : " + username));
/// /
/// /        String encodedPassword = user.getPassword();
/// /
/// /        if (!passwordEncoder.matches(password, encodedPassword)) {
/// /            throw new BadCredentialsException("User password incorrected");
/// /        }
/// /
/// /        String accessToken = tokenProvider.generateAccessToken(user);
/// /        String refreshToken = tokenProvider.generateRefreshToken(user);
/// /
/// /        TokenDTO tokenDto = TokenDTO.builder()
/// /                .accessToken(accessToken)
/// /                .refreshToken(refreshToken)
/// /                .expiresIn(jwtTokenProvider.getAccessTokenExpiration().toSeconds())
/// /                .authorities(user.getAuthorities().stream()
/// /                        .map(GrantedAuthority::getAuthority)
/// /                        .collect(Collectors.toSet()))
/// /                .username(user.getUsername())
/// /                .build();
/// /
/// /        log.info("User '{}' successfully authenticated and tokens generated.", username);
/// /
/// /        return tokenDto;
/// /    }
/// /
/// /    @Override
/// /    @Transactional
/// /    public String register(RegisterRequest request) {
/// /        if (userRepository.existsByUsername(request.getUsername())) {
/// /            throw new BadRequestException("Bu username allaqachon mavjud");
/// /        }
/// /        if (userRepository.existsByEmail(request.getEmail())) {
/// /            throw new BadRequestException("Bu email allaqachon ro'yxatdan o'tgan");
/// /        }
/// /
/// /        User user = new User();
/// /        user.setUsername(request.getUsername());
/// /        user.setPassword(passwordEncoder.encode(request.getPassword()));
/// /        user.setRole(User.Role.USER);
/// /        user.setIsActive(true);
/// /        user.setEmailVerified(false);
/// /
/// /        user.setEmail(request.getEmail());
/// /
/// /        UserProfile userProfile = new UserProfile();
/// /
/// /        userProfile.setUsername(request.getUsername());
/// /
/// /        userProfile.setEmail(request.getEmail());
/// /
/// /
/// /        String verificationCode = String.format("%06d", new Random().nextInt(999999));
/// /        user.setVerificationCode(verificationCode);
/// /        user.setVerificationCodeExpiry(LocalDateTime.now().plusMinutes(15));
/// /
/// /        sendVerificationEmail(user.getEmail(), verificationCode);
/// /
/// /        userRepository.save(user);
/// /
/// /
/// /        userProfile.setUser(user);
/// /
/// /        userProfileRepository.save(userProfile);
/// /
/// /
/// /        return "Ro'yxatdan o'tish uchun emailingizga tasdiqlash kodi yuborildi";
/// /    }
/// /
/// /    @Override
/// /    @Transactional
/// /    public TokenDTO verifyEmail(VerifyEmailRequest request) {
/// /        User user = userRepository.findByEmail(request.getEmail())
/// /                .orElseThrow(() -> new ResourceNotFoundException("Foydalanuvchi topilmadi"));
/// /
/// /        if (user.getEmailVerified()) {
/// /            throw new BadRequestException("Email allaqachon tasdiqlangan");
/// /        }
/// /
/// /        if (user.getVerificationCode() == null || !user.getVerificationCode().equals(request.getCode())) {
/// /            throw new BadRequestException("Noto'g'ri kod");
/// /        }
/// /
/// /        if (user.getVerificationCodeExpiry().isBefore(LocalDateTime.now())) {
/// /            throw new BadRequestException("Kod muddati tugagan");
/// /        }
/// /
/// /        user.setEmailVerified(true);
/// /        user.setVerificationCode(null);
/// /        user.setVerificationCodeExpiry(null);
/// /
/// /        User savedUser = userRepository.save(user);
/// /
/// /        String token = tokenProvider.generateTokenFromUsername(savedUser.getUsername(), jwtTokenProvider.getAccessTokenExpiration().toMillis());
/// /        String refreshToken = tokenProvider.generateRefreshToken(savedUser);
/// /
/// /        UserResponse userResponse = modelMapper.map(savedUser, UserResponse.class);
/// /        userResponse.setRole(savedUser.getRole().name());
/// /
/// /        return TokenDTO.builder()
/// /                .accessToken(token)
/// /                .refreshToken(refreshToken)
/// /                .expiresIn(jwtTokenProvider.getAccessTokenExpiration().toSeconds())
/// /                .authorities(user.getAuthorities().stream()
/// /                        .map(GrantedAuthority::getAuthority)
/// /                        .collect(Collectors.toSet()))
/// /                .username(user.getUsername())
/// /                .build();
/// /    }
/// /
/// /    private void sendVerificationEmail(String email, String code) {
/// /        try {
/// /            SimpleMailMessage message = new SimpleMailMessage();
/// /            message.setTo(email);
/// /            message.setSubject("Ro'yxatdan o'tishni tasdiqlash");
/// /            message.setText("Sizning tasdiqlash kodingiz: " + code + "\n\n" +
/// /                    "Bu kod 15 daqiqa amal qiladi.");
/// /
/// /            mailSender.send(message);
/// /        } catch (Exception e) {
/// /            System.err.println("Email yuborishda xatolik: " + e.getMessage());
/// /        }
/// /    }
/// /
/// /
/// /    @Override
/// /    @Transactional
/// /    public void forgotPassword(ForgotPasswordRequest request) {
/// /        User user = userRepository.findByEmail(request.getEmail())
/// /                .orElseThrow(() -> new ResourceNotFoundException("Bu email bilan foydalanuvchi topilmadi"));
/// /
/// /        String resetToken = UUID.randomUUID().toString();
/// /        user.setResetToken(resetToken);
/// /        user.setResetTokenExpiry(LocalDateTime.now().plusHours(24));
/// /
/// /        userRepository.save(user);
/// /
/// /        sendPasswordResetEmail(user.getEmail(), resetToken);
/// /    }
/// /
/// /    @Override
/// /    @Transactional
/// /    public void resetPassword(ResetPasswordRequest request) {
/// /
/// /        User user = userRepository.findByResetToken(request.getToken())
/// /                .orElseThrow(() -> new BadRequestException("Noto'g'ri yoki muddati o'tgan token"));
/// /
/// /        if (user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
/// /            throw new BadRequestException("Token muddati tugagan");
/// /        }
/// /
/// /        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
/// /        user.setResetToken(null);
/// /        user.setResetTokenExpiry(null);
/// /
/// /        userRepository.save(user);
/// /    }
/// /
/// /    @Override
/// /    public Object loadUserByUsername(String email) {
/// /        return null;
/// /    }
/// /
/// /    private void sendPasswordResetEmail(String email, String token) {
/// /        try {
/// /            SimpleMailMessage message = new SimpleMailMessage();
/// /            message.setTo(email);
/// /            message.setSubject("Parolni tiklash");
/// /            message.setText("Parolni tiklash uchun quyidagi tokenni oling:\n\n" + token + "\n\n" +
/// /                    "Bu token 24 soat amal qiladi.");
/// /
/// /            mailSender.send(message);
/// /        } catch (Exception e) {
/// /
/// /            System.err.println("Email yuborishda xatolik: " + e.getMessage());
/// /        }
/// /    }
/// /}
//
//package uz.literature.platform.service.impl;
//
//import jakarta.persistence.EntityNotFoundException;
//import lombok.extern.slf4j.Slf4j;
//import org.modelmapper.ModelMapper;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.mail.SimpleMailMessage;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.security.authentication.BadCredentialsException;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import uz.literature.platform.entity.User;
//import uz.literature.platform.entity.UserProfile;
//import uz.literature.platform.exception.BadRequestException;
//import uz.literature.platform.exception.ResourceNotFoundException;
//import uz.literature.platform.payload.request.*;
//import uz.literature.platform.payload.response.TokenDTO;
//import uz.literature.platform.payload.response.UserResponse;
//import uz.literature.platform.repository.UserProfileRepository;
//import uz.literature.platform.repository.UserRepository;
//import uz.literature.platform.security.JwtTokenProvider;
//import uz.literature.platform.service.JwtTokenProperties;
//import uz.literature.platform.service.interfaces.AuthService;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Optional;
//import java.util.Random;
//import java.util.UUID;
//import java.util.stream.Collectors;
//
//@Service
//@Slf4j
//public class AuthServiceImpl implements AuthService, UserDetailsService {
//
//    private final UserRepository userRepository;
//    private final PasswordEncoder passwordEncoder;
//    private final JwtTokenProvider tokenProvider;
//    private final JavaMailSender mailSender;
//    private final JwtTokenProperties jwtTokenProperties;
//    private final UserProfileRepository userProfileRepository;
//
//    @Autowired
//    public AuthServiceImpl(UserRepository userRepository,
//                           PasswordEncoder passwordEncoder,
//                           JwtTokenProvider tokenProvider,
//                           JavaMailSender mailSender,
//                           JwtTokenProperties jwtTokenProperties,
//                           UserProfileRepository userProfileRepository) {
//        this.userRepository = userRepository;
//        this.passwordEncoder = passwordEncoder;
//        this.tokenProvider = tokenProvider;
//        this.mailSender = mailSender;
//        this.jwtTokenProperties = jwtTokenProperties;
//        this.userProfileRepository = userProfileRepository;
//    }
//
//    @Override
//    public UserDetails loadUserByUsername(String usernameOrEmail) {
//        log.info("loadUserByUsername chaqirildi: {}", usernameOrEmail);
//
//        User user = userRepository.findByUsername(usernameOrEmail)
//                .orElse(userRepository.findByEmail(usernameOrEmail)
//                        .orElseThrow(() -> new UsernameNotFoundException(
//                                "Foydalanuvchi topilmadi: " + usernameOrEmail)));
//
//        List<SimpleGrantedAuthority> authorities = user.getAuthorities()
//                .stream()
//                .map(grantedAuthority ->  new SimpleGrantedAuthority(grantedAuthority.getAuthority()))
//                .collect(Collectors.toList());
//
//        log.info("User yuklandi: {} (role: {})", user.getUsername(), user.getRole());
//        return org.springframework.security.core.userdetails.User.builder()
//                .username(user.getUsername())
//                .password(user.getPassword())
//                .authorities(authorities)
//                .accountExpired(false)
//                .accountLocked(false)
//                .credentialsExpired(false)
//                .disabled(!user.getIsActive())
//                .build();
//    }
//
//    @Override
//    @Transactional
//    public TokenDTO login(LoginRequest request) {
//        String usernameOrEmail = request.getUsernameOrEmail();
//
//        Optional<User> byUsername = userRepository.findByUsername(usernameOrEmail);
//
//        User userDetails = byUsername.get();
//
//        User user = userRepository.findByUsername(userDetails.getUsername())
//                .orElseThrow(() -> new EntityNotFoundException("User not found"));
//
//        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
//            throw new BadCredentialsException("Parol noto'g'ri");
//        }
//
//        String accessToken = tokenProvider.generateAccessToken((UserDetails) userDetails);
//        String refreshToken = tokenProvider.generateRefreshToken(userDetails);
//
//        TokenDTO tokenDto = TokenDTO.builder()
//                .accessToken(accessToken)
//                .refreshToken(refreshToken)
//                .expiresIn(jwtTokenProperties.getAccessTokenExpiration().toSeconds())
//                .authorities(userDetails.getAuthorities().stream()
//                        .map(GrantedAuthority::getAuthority)
//                        .collect(Collectors.toSet()))
//                .username(user.getUsername())
//                .build();
//
//        log.info("User '{}' successfully authenticated and tokens generated.", user.getUsername());
//
//        return tokenDto;
//    }
//
//    @Override
//    @Transactional
//    public String register(RegisterRequest request) {
//        if (userRepository.existsByUsername(request.getUsername())) {
//            throw new BadRequestException("Bu username allaqachon mavjud");
//        }
//        if (userRepository.existsByEmail(request.getEmail())) {
//            throw new BadRequestException("Bu email allaqachon ro'yxatdan o'tgan");
//        }
//
//        User user = new User();
//        user.setUsername(request.getUsername());
//        user.setPassword(passwordEncoder.encode(request.getPassword()));
//        user.setRole(User.Role.USER);
//        user.setIsActive(true);
//        user.setEmailVerified(false);
//        user.setEmail(request.getEmail());
//
//        UserProfile userProfile = new UserProfile();
//        userProfile.setUsername(request.getUsername());
//        userProfile.setEmail(request.getEmail());
//
//        String verificationCode = String.format("%06d", new Random().nextInt(999999));
//        user.setVerificationCode(verificationCode);
//        user.setVerificationCodeExpiry(LocalDateTime.now().plusMinutes(15));
//
//        User savedUser = userRepository.save(user);
//
//        userProfile.setUser(savedUser);
//
//        userProfileRepository.save(userProfile);
//
//        sendVerificationEmail(user.getEmail(), verificationCode);
//
//        return "Ro'yxatdan o'tish uchun emailingizga tasdiqlash kodi yuborildi";
//    }
//
//    @Override
//    @Transactional
//    public TokenDTO verifyEmail(VerifyEmailRequest request) {
//        User user = userRepository.findByEmail(request.getEmail())
//                .orElseThrow(() -> new ResourceNotFoundException("Foydalanuvchi topilmadi"));
//
//        if (user.getEmailVerified()) {
//            throw new BadRequestException("Email allaqachon tasdiqlangan");
//        }
//
//        if (user.getVerificationCode() == null || !user.getVerificationCode().equals(request.getCode())) {
//            throw new BadRequestException("Noto'g'ri kod");
//        }
//
//        if (user.getVerificationCodeExpiry().isBefore(LocalDateTime.now())) {
//            throw new BadRequestException("Kod muddati tugagan");
//        }
//
//// verifyEmail ichida, oxirgi qismni shunday o‘zgartiring:
//
//        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
//                .username(user.getUsername())           // "barkamol123"
//                .password(user.getPassword())
//                .authorities(user.getAuthorities())
//                .accountExpired(false)
//                .accountLocked(false)
//                .credentialsExpired(false)
//                .disabled(!user.getIsActive())
//                .build();
//
//        String accessToken = tokenProvider.generateAccessToken(userDetails);
//        String refreshToken = tokenProvider.generateRefreshToken(userDetails);
//
//        return TokenDTO.builder()
//                .accessToken(accessToken)
//                .refreshToken(refreshToken)
//                .expiresIn(jwtTokenProperties.getAccessTokenExpiration().toSeconds())
//                .authorities(userDetails.getAuthorities().stream()
//                        .map(GrantedAuthority::getAuthority)
//                        .collect(Collectors.toSet()))
//                .username(user.getUsername())
//                .build();
//    }
//
//    private void sendVerificationEmail(String email, String code) {
//        try {
//            SimpleMailMessage message = new SimpleMailMessage();
//            message.setTo(email);
//            message.setSubject("Ro'yxatdan o'tishni tasdiqlash");
//            message.setText("Sizning tasdiqlash kodingiz: " + code + "\n\n" +
//                    "Bu kod 15 daqiqa amal qiladi.");
//
//            mailSender.send(message);
//        } catch (Exception e) {
//            System.err.println("Email yuborishda xatolik: " + e.getMessage());
//        }
//    }
//
//    @Override
//    @Transactional
//    public void forgotPassword(ForgotPasswordRequest request) {
//        User user = userRepository.findByEmail(request.getEmail())
//                .orElseThrow(() -> new ResourceNotFoundException("Bu email bilan foydalanuvchi topilmadi"));
//
//        String resetToken = UUID.randomUUID().toString();
//        user.setResetToken(resetToken);
//        user.setResetTokenExpiry(LocalDateTime.now().plusHours(24));
//
//        userRepository.save(user);
//
//        sendPasswordResetEmail(user.getEmail(), resetToken);
//    }
//
//    @Override
//    @Transactional
//    public void resetPassword(ResetPasswordRequest request) {
//        User user = userRepository.findByResetToken(request.getToken())
//                .orElseThrow(() -> new BadRequestException("Noto'g'ri yoki muddati o'tgan token"));
//
//        if (user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
//            throw new BadRequestException("Token muddati tugagan");
//        }
//
//        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
//        user.setResetToken(null);
//        user.setResetTokenExpiry(null);
//
//        userRepository.save(user);
//    }
//
//    private void sendPasswordResetEmail(String email, String token) {
//        try {
//            SimpleMailMessage message = new SimpleMailMessage();
//            message.setTo(email);
//            message.setSubject("Parolni tiklash");
//            message.setText("Parolni tiklash uchun quyidagi tokenni oling:\n\n" + token + "\n\n" +
//                    "Bu token 24 soat amal qiladi.");
//
//            mailSender.send(message);
//        } catch (Exception e) {
//            System.err.println("Email yuborishda xatolik: " + e.getMessage());
//        }
//    }
//}

package uz.literature.platform.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.literature.platform.entity.User;
import uz.literature.platform.entity.UserProfile;
import uz.literature.platform.exception.BadRequestException;
import uz.literature.platform.exception.ResourceNotFoundException;
import uz.literature.platform.payload.request.*;
import uz.literature.platform.payload.response.PendingRegistration;
import uz.literature.platform.payload.response.TokenDTO;
import uz.literature.platform.repository.UserProfileRepository;
import uz.literature.platform.repository.UserRepository;
import uz.literature.platform.security.JwtTokenProvider;
import uz.literature.platform.service.JwtTokenProperties;
import uz.literature.platform.service.interfaces.AuthService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService, UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final JavaMailSender mailSender;
    private final JwtTokenProperties jwtTokenProperties;
    private final UserProfileRepository userProfileRepository;
    private final PendingRegistrationService pendingRegistrationService;

    @Autowired
    public AuthServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           JwtTokenProvider tokenProvider,
                           JavaMailSender mailSender,
                           JwtTokenProperties jwtTokenProperties,
                           UserProfileRepository userProfileRepository,
                           PendingRegistrationService pendingRegistrationService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.mailSender = mailSender;
        this.jwtTokenProperties = jwtTokenProperties;
        this.userProfileRepository = userProfileRepository;
        this.pendingRegistrationService = pendingRegistrationService;
    }

    //    @Override
//    public UserDetails loadUserByUsername(String usernameOrEmail) {
//        log.info("loadUserByUsername chaqirildi: {}", usernameOrEmail);
//
//        User user = userRepository.findByUsername(usernameOrEmail)
//                .orElse(userRepository.findByEmail(usernameOrEmail)
//                        .orElseThrow(() -> new UsernameNotFoundException(
//                                "Foydalanuvchi topilmadi: " + usernameOrEmail)));
//
//        List<SimpleGrantedAuthority> authorities = user.getAuthorities()
//                .stream()
//                .map(grantedAuthority -> new SimpleGrantedAuthority(grantedAuthority.getAuthority()))
//                .collect(Collectors.toList());
//
//        log.info("User yuklandi: {} (role: {})", user.getUsername(), user.getRole());
//        return org.springframework.security.core.userdetails.User.builder()
//                .username(user.getUsername())
//                .password(user.getPassword())
//                .authorities(authorities)
//                .accountExpired(false)
//                .accountLocked(false)
//                .credentialsExpired(false)
//                .disabled(!user.getIsActive())
//                .build();
//    }
    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) {
        log.info("loadUserByUsername chaqirildi: {}", usernameOrEmail);

        User user;

        // Agar @ belgisi bo'lsa, bu email - avval email bo'yicha qidirish
        if (usernameOrEmail.contains("@")) {
            user = userRepository.findByEmail(usernameOrEmail)
                    .orElseThrow(() -> new UsernameNotFoundException(
                            "Foydalanuvchi topilmadi: " + usernameOrEmail));
        } else {
            // Aks holda username bo'yicha qidirish, topilmasa email bo'yicha
            user = userRepository.findByUsername(usernameOrEmail)
                    .orElseGet(() -> userRepository.findByEmail(usernameOrEmail)
                            .orElseThrow(() -> new UsernameNotFoundException(
                                    "Foydalanuvchi topilmadi: " + usernameOrEmail)));
        }

        List<SimpleGrantedAuthority> authorities = user.getAuthorities()
                .stream()
                .map(grantedAuthority -> new SimpleGrantedAuthority(grantedAuthority.getAuthority()))
                .collect(Collectors.toList());

        log.info("User yuklandi: {} (role: {})", user.getUsername(), user.getRole());

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(!user.getIsActive())
                .build();
    }

    @Override
    @Transactional
    public TokenDTO login(LoginRequest request) {
        String usernameOrEmail = request.getUsernameOrEmail();

        Optional<User> byUsername = userRepository.findByUsername(usernameOrEmail);

        User userDetails = byUsername.get();

        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Parol noto'g'ri");
        }

        String accessToken = tokenProvider.generateAccessToken((UserDetails) userDetails);
        String refreshToken = tokenProvider.generateRefreshToken(userDetails);

        TokenDTO tokenDto = TokenDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(jwtTokenProperties.getAccessTokenExpiration().toSeconds())
                .authorities(userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toSet()))
                .username(user.getUsername())
                .build();

        log.info("User '{}' successfully authenticated and tokens generated.", user.getUsername());

        return tokenDto;
    }

    @Override
    @Transactional
    public String register(RegisterRequest request) {
        // 1. Avval bazada mavjudligini tekshirish
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Bu username allaqachon mavjud");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Bu email allaqachon ro'yxatdan o'tgan");
        }

        // 2. Agar eski pending registration bo'lsa, o'chirish
        if (pendingRegistrationService.hasPendingRegistration(request.getEmail())) {
            pendingRegistrationService.removePendingRegistration(request.getEmail());
            log.info("Old pending registration removed for: {}", request.getEmail());
        }

        // 3. Verification code generatsiya qilish
        String verificationCode = String.format("%06d", new Random().nextInt(999999));

        // 4. Pending registration yaratish (hali bazaga saqlamaymiz!)
        PendingRegistration pendingRegistration = PendingRegistration.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .encodedPassword(passwordEncoder.encode(request.getPassword()))
                .verificationCode(verificationCode)
                .expiryTime(LocalDateTime.now().plusMinutes(15))
                .build();

        // 5. Vaqtinchalik xotirada saqlash
        pendingRegistrationService.savePendingRegistration(request.getEmail(), pendingRegistration);

        // 6. Email yuborish
        sendVerificationEmail(request.getEmail(), verificationCode);

        log.info("Registration initiated for: {} (pending verification)", request.getEmail());

        return "Ro'yxatdan o'tish uchun emailingizga tasdiqlash kodi yuborildi. Kod 15 daqiqa amal qiladi.";
    }

    @Override
    @Transactional
    public TokenDTO verifyEmail(VerifyEmailRequest request) {
        // 1. Pending registrationni olish
        uz.literature.platform.payload.response.PendingRegistration pendingReg = pendingRegistrationService.getPendingRegistration(request.getEmail());

        if (pendingReg == null) {
            throw new ResourceNotFoundException("Bu email uchun kutilayotgan ro'yxatdan o'tish topilmadi. Iltimos qaytadan ro'yxatdan o'ting.");
        }

        // 2. Kodni tekshirish
        if (!pendingReg.getVerificationCode().equals(request.getCode())) {
            throw new BadRequestException("Noto'g'ri kod");
        }

        // 3. Muddatni tekshirish
        if (pendingReg.getExpiryTime().isBefore(LocalDateTime.now())) {
            pendingRegistrationService.removePendingRegistration(request.getEmail());
            throw new BadRequestException("Kod muddati tugagan. Iltimos qaytadan ro'yxatdan o'ting.");
        }

        // 4. Yana bir marta username va email mavjudligini tekshirish (xavfsizlik uchun)
        if (userRepository.existsByUsername(pendingReg.getUsername())) {
            pendingRegistrationService.removePendingRegistration(request.getEmail());
            throw new BadRequestException("Bu username allaqachon band qilingan");
        }
        if (userRepository.existsByEmail(pendingReg.getEmail())) {
            pendingRegistrationService.removePendingRegistration(request.getEmail());
            throw new BadRequestException("Bu email allaqachon ro'yxatdan o'tgan");
        }

        // 5. ENDi bazaga saqlash - kod to'g'ri ekan!
        User user = new User();
        user.setUsername(pendingReg.getUsername());
        user.setEmail(pendingReg.getEmail());
        user.setPassword(pendingReg.getEncodedPassword());
        user.setRole(User.Role.USER);
        user.setIsActive(true);
        user.setEmailVerified(true); // Kod tasdiqlandi, shuning uchun true
        user.setVerificationCode(null);
        user.setVerificationCodeExpiry(null);

        User savedUser = userRepository.save(user);

        // 6. UserProfile yaratish
        UserProfile userProfile = new UserProfile();
        userProfile.setUsername(pendingReg.getUsername());
        userProfile.setEmail(pendingReg.getEmail());
        userProfile.setUser(savedUser);
        userProfileRepository.save(userProfile);

        // 7. Pending registrationni o'chirish
        pendingRegistrationService.removePendingRegistration(request.getEmail());

        // 8. Token generatsiya qilish
        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(user.getAuthorities())
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(!user.getIsActive())
                .build();

        String accessToken = tokenProvider.generateAccessToken(userDetails);
        String refreshToken = tokenProvider.generateRefreshToken(userDetails);

        log.info("User successfully registered and verified: {}", user.getUsername());

        return TokenDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(jwtTokenProperties.getAccessTokenExpiration().toSeconds())
                .authorities(userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toSet()))
                .username(user.getUsername())
                .build();
    }

    private void sendVerificationEmail(String email, String code) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Ro'yxatdan o'tishni tasdiqlash");
            message.setText("Sizning tasdiqlash kodingiz: " + code + "\n\n" +
                    "Bu kod 15 daqiqa amal qiladi.");

            mailSender.send(message);
            log.info("Verification email sent to: {}", email);
        } catch (Exception e) {
            log.error("Email yuborishda xatolik: {}", e.getMessage());
            throw new RuntimeException("Email yuborib bo'lmadi");
        }
    }

    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Bu email bilan foydalanuvchi topilmadi"));

        String resetToken = UUID.randomUUID().toString();
        user.setResetToken(resetToken);
        user.setResetTokenExpiry(LocalDateTime.now().plusHours(24));

        userRepository.save(user);

        sendPasswordResetEmail(user.getEmail(), resetToken);
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByResetToken(request.getToken())
                .orElseThrow(() -> new BadRequestException("Noto'g'ri yoki muddati o'tgan token"));

        if (user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Token muddati tugagan");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);

        userRepository.save(user);
    }

    private void sendPasswordResetEmail(String email, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Parolni tiklash");
            message.setText("Parolni tiklash uchun quyidagi tokenni oling:\n\n" + token + "\n\n" +
                    "Bu token 24 soat amal qiladi.");

            mailSender.send(message);
        } catch (Exception e) {
            log.error("Email yuborishda xatolik: {}", e.getMessage());
        }
    }
}