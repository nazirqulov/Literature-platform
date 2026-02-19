package uz.literature.platform.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

/**
 * Created by: Barkamol
 * DateTime: 2/12/2026 10:42 AM
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//
//        String uploadPath = Paths.get(uploadDir)
//                .toAbsolutePath()
//                .normalize()
//                .toUri()
//                .toString();
//
//        registry.addResourceHandler("/profiles/**")
//                .addResourceLocations(uploadPath);
        String uploadPath = Paths.get("uploads/profiles")
                .toAbsolutePath()
                .toUri()
                .toString();

        registry.addResourceHandler("/profiles/**")
                .addResourceLocations(uploadPath);

    }
//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        // URL pattern /profiles/** ga mos keladigan fayllar diskdagi uploads/profiles papkasidan olinadi
//        registry.addResourceHandler("")
//                .addResourceLocations("file:uploads/");
//    }

//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler("/profiles/**")
//                .addResourceLocations("file:///uploads/")   // uchta / bilan sinab ko'ring
//                .addResourceLocations("file:/uploads/profiles/")     // ikkita variant
//                .setCachePeriod(3600);
//    }
}
