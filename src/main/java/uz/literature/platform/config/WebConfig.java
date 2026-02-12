package uz.literature.platform.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Created by: Barkamol
 * DateTime: 2/12/2026 10:42 AM
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // URL pattern /profiles/** ga mos keladigan fayllar diskdagi uploads/profiles papkasidan olinadi
        registry.addResourceHandler("/profiles/**")
                .addResourceLocations("file:///C:/Users/user/Desktop/literature-platform/uploads/");
    }
}
