package com.example.sepdrivebackend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {


    @Override
    public void addResourceHandlers (ResourceHandlerRegistry registry) {
        String assetsResourceLocation = "classpath:/static/assets/";
        System.out.println("▶ Serving static files from URL '/assets/**' mapped to: " + assetsResourceLocation);
        registry
                .addResourceHandler("/assets/**") // Anfragen an /assets/...
                .addResourceLocations(assetsResourceLocation);
    }
    
}
