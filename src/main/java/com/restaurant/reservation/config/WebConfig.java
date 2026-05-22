package com.restaurant.reservation.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload.dir:uploads/restaurants}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Resolve the root upload folder (parent-parent of uploadDir)
        // e.g. uploadDir = D:/Reservation/uploads/restaurants
        //      root       = D:/Reservation
        // So /uploads/** maps to D:/Reservation/uploads/
        String uploadRoot = Paths.get(uploadDir).toAbsolutePath()
                .getParent()  // D:/Reservation/uploads
                .getParent()  // D:/Reservation
                .resolve("uploads")
                .toUri()
                .toString();

        // Ensure it ends with /
        if (!uploadRoot.endsWith("/")) {
            uploadRoot = uploadRoot + "/";
        }

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadRoot);
    }
}
