package com.dubroushchyk.clever;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class CleverApplication {

    public static void main(String[] args) {
        SpringApplication.run(CleverApplication.class, args);
    }

}
