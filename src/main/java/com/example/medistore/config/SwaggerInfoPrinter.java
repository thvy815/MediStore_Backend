package com.example.medistore.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SwaggerInfoPrinter implements CommandLineRunner {

    @Value("${server.port:8080}")
    private String port;

    @Override
    public void run(String... args) {

        System.out.println("\n=================================");
        System.out.println("MediStore Backend Started");
        System.out.println("Swagger UI: http://localhost:" + port + "/swagger-ui/index.html");
        System.out.println("=================================\n");
    }
}