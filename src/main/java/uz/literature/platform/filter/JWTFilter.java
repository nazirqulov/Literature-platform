//package uz.literature.platform.filter;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import jakarta.validation.constraints.NotNull;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//import uz.literature.platform.entity.User;
//import uz.literature.platform.security.JwtTokenProvider;
//import uz.literature.platform.service.interfaces.AuthService;
//
//import java.io.IOException;
//import java.util.Objects;
//
//
//@Component
//@Slf4j
//@RequiredArgsConstructor
//public class JWTFilter extends OncePerRequestFilter {
//
//    private final JwtTokenProvider jwtService;
//
//    private final AuthService authService;
//
//
//    @Override
//    protected void doFilterInternal(@NotNull HttpServletRequest request,
//                                    @NotNull HttpServletResponse response,
//                                    FilterChain filterChain) throws ServletException, IOException {
//
//
//        String path = request.getRequestURI();
//
//        if (path.startsWith("/ws")) {
//
//            filterChain.doFilter(request, response);
//
//            return;
//        }
//
//        checkBearer(request, response);
//
//        filterChain.doFilter(request, response);
//    }
//
//    private void checkBearer(HttpServletRequest request, HttpServletResponse response) throws IOException {
//
//        String authorization = request.getHeader("Authorization");
//
//        if (Objects.isNull(authorization) || !authorization.startsWith("Bearer ")) {
//
//            request.setAttribute("JWT_ERROR", "Missing or invalid Authorization header");
//            return;
//
//        }
//
//        String token = authorization.substring(7);
//
//        try {
//
//            String email = jwtService.extractUsername(token);
//
//            User user = (User) authService.loadUserByUsername(email);
//
//            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
//                    user.getRole(),
//                    user,
//                    null
//            ));
//        } catch (io.jsonwebtoken.ExpiredJwtException e) {
//
//            request.setAttribute("JWT_ERROR", "Token expired");
//
//        } catch (Exception e) {
//
//            e.printStackTrace();
//
//            request.setAttribute("JWT_ERROR", "Invalid token");
//
//        }
//    }
//}

package uz.literature.platform.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import uz.literature.platform.security.JwtTokenProvider;
import uz.literature.platform.service.interfaces.AuthService;

import java.io.IOException;
import java.util.Objects;

@Component
@Slf4j
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtService;
    private final AuthService authService;

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();

        if (path.startsWith("/ws")) {
            filterChain.doFilter(request, response);
            return;
        }

        checkBearer(request, response);

        filterChain.doFilter(request, response);
    }

    private void checkBearer(HttpServletRequest request, HttpServletResponse response) {

        String authorization = request.getHeader("Authorization");

        if (Objects.isNull(authorization) || !authorization.startsWith("Bearer ")) {
            request.setAttribute("JWT_ERROR", "Missing or invalid Authorization header");
            return;
        }

        String token = authorization.substring(7);

        try {
            String username = jwtService.extractUsername(token);

            UserDetails userDetails = (UserDetails) authService.loadUserByUsername(username);

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );

            SecurityContextHolder.getContext().setAuthentication(authToken);

        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            request.setAttribute("JWT_ERROR", "Token expired");
        } catch (Exception e) {
            log.warn("JWT validation failed", e);
            request.setAttribute("JWT_ERROR", "Invalid token");
        }
    }
}