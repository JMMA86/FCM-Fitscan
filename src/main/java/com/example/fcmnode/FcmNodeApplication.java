package com.example.fcmnode;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FcmNodeApplication {

    public static void main(String[] args) {
        SpringApplication.run(FcmNodeApplication.class, args);
    }

}
