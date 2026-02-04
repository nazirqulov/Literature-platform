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
        description = "JWT tokenni Bearer prefiksiz bilan kiriting eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...)",
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
