package uz.literature.platform.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.literature.platform.payload.request.*;
import uz.literature.platform.payload.response.AuthResponse;
import uz.literature.platform.payload.response.TokenDTO;
import uz.literature.platform.payload.response.UserResponse;
import uz.literature.platform.entity.User;
import uz.literature.platform.exception.BadRequestException;
import uz.literature.platform.exception.ResourceNotFoundException;
import uz.literature.platform.repository.UserRepository;
import uz.literature.platform.security.JwtTokenProvider;
import uz.literature.platform.service.JwtTokenProperties;
import uz.literature.platform.service.interfaces.AuthService;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final ModelMapper modelMapper;
    private final JavaMailSender mailSender;

    private final JwtTokenProperties jwtTokenProvider;

    @Autowired
    public AuthServiceImpl(UserRepository userRepository, 
                          PasswordEncoder passwordEncoder,
                          AuthenticationManager authenticationManager,
                          JwtTokenProvider tokenProvider,
                          ModelMapper modelMapper,
                          JavaMailSender mailSender,
                           JwtTokenProperties jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.modelMapper = modelMapper;
        this.mailSender = mailSender;
        this.jwtTokenProvider = jwtTokenProvider;
    }
    
    @Override
    @Transactional
    public TokenDTO login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsernameOrEmail(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Foydalanuvchi topilmadi"));

        String token = tokenProvider.generateAccessToken(user);
        String refreshToken = tokenProvider.generateRefreshToken(user);

        System.out.println("token = " + token);
        UserResponse userResponse = modelMapper.map(user, UserResponse.class);
        userResponse.setRole(user.getRole().name());

        return new TokenDTO(token,refreshToken,user.getRole().name());
    }
    
    @Override
    @Transactional
    public String register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Bu username allaqachon mavjud");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Bu email allaqachon ro'yxatdan o'tgan");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setRole(User.Role.USER);
        user.setIsActive(true);
        user.setEmailVerified(false);

        String verificationCode = String.format("%06d", new Random().nextInt(999999));
        user.setVerificationCode(verificationCode);
        user.setVerificationCodeExpiry(LocalDateTime.now().plusMinutes(15));

        userRepository.save(user);

        sendVerificationEmail(user.getEmail(), verificationCode);

        return "Ro'yxatdan o'tish uchun emailingizga tasdiqlash kodi yuborildi";
    }

    @Override
    @Transactional
    public TokenDTO verifyEmail(VerifyEmailRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Foydalanuvchi topilmadi"));

        if (user.getEmailVerified()) {
            throw new BadRequestException("Email allaqachon tasdiqlangan");
        }

        if (user.getVerificationCode() == null || !user.getVerificationCode().equals(request.getCode())) {
            throw new BadRequestException("Noto'g'ri kod");
        }

        if (user.getVerificationCodeExpiry().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Kod muddati tugagan");
        }

        user.setEmailVerified(true);
        user.setVerificationCode(null);
        user.setVerificationCodeExpiry(null);

        User savedUser = userRepository.save(user);

        String token = tokenProvider.generateTokenFromUsername(savedUser.getUsername(),jwtTokenProvider.getAccessTokenExpiration().toMillis());
        String refreshToken = tokenProvider.generateRefreshToken(savedUser);

        UserResponse userResponse = modelMapper.map(savedUser, UserResponse.class);
        userResponse.setRole(savedUser.getRole().name());

        return new TokenDTO(token, refreshToken,savedUser.getRole().name());
    }

    private void sendVerificationEmail(String email, String code) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Ro'yxatdan o'tishni tasdiqlash");
            message.setText("Sizning tasdiqlash kodingiz: " + code + "\n\n" +
                            "Bu kod 15 daqiqa amal qiladi.");
            
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Email yuborishda xatolik: " + e.getMessage());
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

    @Override
    public Object loadUserByUsername(String email) {
        return null;
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

            System.err.println("Email yuborishda xatolik: " + e.getMessage());
        }
    }
}
