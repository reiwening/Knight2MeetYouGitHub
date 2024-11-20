package com.g5.cs203proj.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Enable CORS for all routes
        .allowedOrigins(
                    "http://localhost:3000",           // Local development
                    "http://35.240.208.160",          // Production frontend (static IP)
                    "http://35.240.208.160:80"        // With port specified
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*") // Allow all headers
                .exposedHeaders("Authorization", "Content-Type") // Expose any additional headers if needed
                .allowCredentials(true);
    }
}
