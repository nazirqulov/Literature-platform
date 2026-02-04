package uz.literature.platform.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultClaims;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import uz.literature.platform.entity.User;
import uz.literature.platform.service.JwtTokenProperties;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
@Service
@RequiredArgsConstructor
//public class JwtTokenProvider {
//
//    @Value("${jwt.secret}")
//    private String jwtSecret;
//
//    @Value("${jwt.expiration}")
//    private long jwtExpiration;
//
//    private Key signingKey;
//
//    private final JwtTokenProperties jwtTokenProperties;
//
//    private SecretKey key;
//    @PostConstruct
//    public void init() {
//
//        byte[] keyBytes = Decoders.BASE64.decode(jwtTokenProperties.getSecret());
//        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
//    }
//
//
//
//    /**
//     * Foydalanuvchi ma'lumotlari asosida Access Token yaratadi.
//     */
//    public String generateAccessToken(UserDetails userDetails) {
//        Map<String, Object> extraClaims = new HashMap<>();
//
//        extraClaims.put("roles", userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList());
//
//
//        if (userDetails instanceof User user) {
//            extraClaims.put("userId", user.getId());
////            extraClaims.put("profileId",user.getProfile().getId()); // todo this is incorrected
//        }
//
//        return buildToken(extraClaims, userDetails, jwtTokenProperties.getAccessTokenExpiration());
//    }
//
//    /**
//     * Foydalanuvchi ma'lumotlari asosida Refresh Token yaratadi.
//     * Refresh token odatda qo'shimcha ma'lumotlarsiz bo'ladi.
//     */
//    public String generateRefreshToken(UserDetails userDetails) {
//        return buildToken(new HashMap<>(), userDetails, jwtTokenProperties.getRefreshTokenExpiration());
//    }
//
//    private String buildToken(
//            Map<String, Object> extraClaims,
//            UserDetails userDetails,
//            Duration expiration
//    ) {
//        Date now = new Date();
//        Date expiryDate = new Date(now.getTime() + expiration.toMillis());
//
//        return Jwts.builder()
//                .setClaims(extraClaims)
//                .setSubject(userDetails.getUsername())
//                .setIssuedAt(now)
//                .setExpiration(expiryDate)
//                .signWith(SignatureAlgorithm.HS256, jwtSecret)
//                .compact();
//    }
//
//
//
//    public String extractUsername(String token) {
//        return extractClaim(token, Claims::getSubject);
//    }
//
//    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
//        final Claims claims = extractAllClaims(token);
//        return claimsResolver.apply(claims);
//    }
//
//    private Claims extractAllClaims(String token) {
//        return Jwts.parserBuilder()
//                .setSigningKey(jwtSecret)
//                .build()
//                .parseClaimsJws(token)
//                .getBody();
//    }
//
//
//
//    /**
//     * Tokenning yaroqliligini (validligini) tekshiradi.
//     * Username to'g'ri kelishi va tokenning muddati o'tmagan bo'lishi kerak.
//     */
//    public boolean isTokenValid(String token, UserDetails userDetails) {
//        final String username = extractUsername(token);
//        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
//    }
//
//    /**
//     * Tokenning yashash muddati o'tgan yoki o'tmaganligini tekshiradi.
//     */
//    private boolean isTokenExpired(String token) {
//        return extractExpiration(token).before(new Date());
//    }
//
//    private Date extractExpiration(String token) {
//        return extractClaim(token, Claims::getExpiration);
//    }
////    @PostConstruct
////    public void init() {
////        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
////    }
////
////    public String generateToken(Authentication authentication) {
////        String username = authentication.getName();
////        Date now = new Date();
////        Date expiryDate = new Date(now.getTime() + jwtExpiration);
////
////        return Jwts.builder()
////                .setSubject(username)
////                .setIssuedAt(now)
////                .setExpiration(expiryDate)
////                .signWith(key)
////                .compact();
////    }
////
////    public String getUsernameFromToken(String token) {
////        Claims claims = Jwts.parserBuilder()
////                .setSigningKey(key)
////                .build()
////                .parseClaimsJws(token)
////                .getBody();
////
////        return claims.getSubject();
////    }
//////    public String parseToken(String token) {
//////
//////        DefaultClaims payload = (DefaultClaims) Jwts.parserBuilder()
//////                .setSigningKey(key())
//////                .build()
//////                .parseClaimsJws(token)
//////                .getBody();
//////
//////        return payload.getSubject();
//////    }
////
////
////    public boolean validateToken(String token) {
////        try {
////            Jwts.parserBuilder()
////                    .setSigningKey(key)
////                    .build()
////                    .parseClaimsJws(token);
////            return true;
////        } catch (JwtException | IllegalArgumentException ex) {
////            return false;
////        }
////    }
////
//    public String generateTokenFromUsername(String username) {
//        Date now = new Date();
//        Date expiryDate = new Date(now.getTime() + jwtExpiration);
//
//        return Jwts.builder()
//                .setSubject(username)
//                .setIssuedAt(now)
//                .setExpiration(expiryDate)
//                .signWith(key)
//                .compact();
//    }
//}

public class JwtTokenProvider {

    private final JwtTokenProperties props;

    private SecretKey key;

    @Value("${jwt.access-token-expiration}")
    private long jwtExpiration;

    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(props.getSecret());
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(UserDetails userDetails) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("roles",
                userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority).toList());

        if (userDetails instanceof User user) {
            extraClaims.put("userId", user.getId());
        }

        return buildToken(extraClaims, userDetails.getUsername(), props.getAccessTokenExpiration());
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return buildToken(new HashMap<>(), userDetails.getUsername(), props.getRefreshTokenExpiration());
    }

    private String buildToken(Map<String, Object> claims, String subject, Duration exp) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + exp.toMillis());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        return resolver.apply(extractAllClaims(token));
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean isTokenValid(String token, UserDetails user) {
        return extractUsername(token).equals(user.getUsername())
                && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    public String generateTokenFromUsername(String username, long expirationMillis) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMillis);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
//    public String generateTokenFromUsername(String username) {
//        Date now = new Date();
//        Date expiryDate = new Date(now.getTime() + jwtExpiration);
//
//        io.jsonwebtoken.JwtBuilder builder = Jwts.builder();
//        builder.setSubject(username);
//        builder.setIssuedAt(now);
//        builder.setExpiration(expiryDate);
//        builder.signWith(props.getSecret());
//        String compact = builder
//                .compact();
//        return compact;
//    }
}
