package com.example.medistore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class MedistoreApplication {

	public static void main(String[] args) {
		 // Load .env vào System properties trước khi Spring Boot start
        Dotenv dotenv = Dotenv.configure()
                              .ignoreIfMissing() // Bỏ qua nếu file .env không tồn tại
                              .load();

        dotenv.entries().forEach(entry -> {
            String key = entry.getKey();
            String value = entry.getValue();
            // Chỉ set nếu chưa tồn tại
            if (System.getenv(key) == null && System.getProperty(key) == null) {
                System.setProperty(key, value);
            }
        });

		SpringApplication.run(MedistoreApplication.class, args);
	}

}
