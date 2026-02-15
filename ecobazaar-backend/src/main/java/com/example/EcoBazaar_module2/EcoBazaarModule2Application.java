package com.example.EcoBazaar_module2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication(scanBasePackages = "com.example.EcoBazaar_module2")
@EntityScan(basePackages = "com.example.EcoBazaar_module2.model")
@EnableJpaRepositories(basePackages = "com.example.EcoBazaar_module2.repository")
public class EcoBazaarModule2Application {

    public static void main(String[] args) {
        SpringApplication.run(EcoBazaarModule2Application.class, args);
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:4200")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
            }
        };
    }
}
