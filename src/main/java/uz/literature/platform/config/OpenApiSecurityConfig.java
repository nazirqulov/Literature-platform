package uz.literature.platform.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(
        name = "bearerAuth",                          // Bu nomni hamma joyda ishlatamiz
        description = "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6WyJST0xFX1VTRVIiXSwidXNlcklkIjoxLCJzdWIiOiJnbG9iYWwiLCJpYXQiOjE3NzA3MTg3MzEsImV4cCI6MTc3MDgwNTEzMX0.63GFMComI6HNxz5DclMouNZb6pRrEbdW9LhbbskiyIo",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
@OpenAPIDefinition(
        info = @Info(title = "Literature Platform API", version = "1.0"),
        security = @SecurityRequirement(name = "bearerAuth")  // GLOBAL qo'llash – hammasi uchun token talab qiladi
)
public class OpenApiSecurityConfig {
}
