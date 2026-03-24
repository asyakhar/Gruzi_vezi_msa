package com.rzd.dispatcher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class Main { // Или ServiceRegistryApplication
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}