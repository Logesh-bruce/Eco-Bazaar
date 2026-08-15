package com.example.EcoBazaar_module2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.example.EcoBazaar_module2")
@EntityScan(basePackages = "com.example.EcoBazaar_module2.model")
@EnableJpaRepositories(basePackages = "com.example.EcoBazaar_module2.repository")
public class EcoBazaarModule2Application {

    public static void main(String[] args) {
        SpringApplication.run(EcoBazaarModule2Application.class, args);
    }
}
