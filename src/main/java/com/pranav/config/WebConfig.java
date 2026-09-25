package com.pranav.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // In development allow requests from local dev servers, tunnels (ngrok), and mobile devices.
        // Use allowedOriginPatterns to support dynamic hosts (e.g., *.ngrok-free.dev)
        registry.addMapping("/**")
                .allowedOriginPatterns("*") // permissive for development; restrict in production
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("Authorization")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
