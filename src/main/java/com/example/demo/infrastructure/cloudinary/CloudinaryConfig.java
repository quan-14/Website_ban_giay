package com.example.demo.infrastructure.cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary configKey() {
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", "drkrb9gk0");
        config.put("api_key", "337213753216156");
        config.put("api_secret", "b0F3Ywut7j7lpvbZiFgNvBZ67zU");
        return new Cloudinary(config);
    }

}
