package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // Applies to ALL endpoints (/api/auth, /api/songs, etc.)
                        // 👇 We added their AWS IP address right here!
                        .allowedOrigins("http://localhost:4200", "http://16.171.141.25")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Allowed request types
                        .allowedHeaders("*") // Allows all headers
                        .allowCredentials(true); // Allows credentials/tokens to be sent
            }
        };
    }
}