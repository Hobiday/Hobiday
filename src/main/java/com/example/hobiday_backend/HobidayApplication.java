package com.example.hobiday_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class HobidayApplication { //323423

    public static void main(String[] args) {
        SpringApplication.run(HobidayApplication.class, args);
    }

}
