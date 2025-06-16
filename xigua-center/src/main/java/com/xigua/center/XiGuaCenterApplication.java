package com.xigua.center;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class XiGuaCenterApplication {

    public static void main(String[] args) {
        SpringApplication.run(XiGuaCenterApplication.class, args);
    }

}
