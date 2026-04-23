package com.volter.shop;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.volter")
public class VolterApplication {

    public static void main(String[] args) {
        // Load .env file only if it exists (for local development)
        // In production, rely on actual environment variables
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing()  // Don't fail if .env doesn't exist (production)
                .load();

        // Only set properties if they're not already set by the environment
        // This allows production environment variables to take precedence
        setPropertyIfPresent(dotenv, "SPRING_DATASOURCE_URL");
        setPropertyIfPresent(dotenv, "SPRING_DATASOURCE_USERNAME");
        setPropertyIfPresent(dotenv, "SPRING_DATASOURCE_PASSWORD");
        setPropertyIfPresent(dotenv, "JWT_CONFIG_SECRET");


        SpringApplication.run(VolterApplication.class, args);
    }

    private static void setPropertyIfPresent(Dotenv dotenv, String key) {
        String value = dotenv.get(key);
        if (value != null && System.getProperty(key) == null) {
            System.setProperty(key, value);
        }
    }
}
