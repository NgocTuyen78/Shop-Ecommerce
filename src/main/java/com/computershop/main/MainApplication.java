package com.computershop.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Main application entry point for the Computer Shop E-commerce system.
 *
 * This application supports:
 * - Single database mode (default)
 * - Distributed database mode (with 2 databases)
 *
 * To switch to distributed mode, run with:
 * --spring.profiles.active=distributed
 *
 * Default credentials:
 * - Admin: admin / admin123
 * - User: user / user123
 */
@SpringBootApplication
@ComponentScan(basePackages = {
    "com.computershop.main",      // Original package with entities, repositories
    "com.computershop.config",    // Configuration classes
    "com.computershop.service",   // Service layer
    "com.computershop.controller", // Controllers
    "com.computershop.exception", // Exception handlers
    "com.computershop.util"       // Utility classes
})
public class MainApplication {

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("Starting Computer Shop Application...");
        System.out.println("=================================================");

        SpringApplication.run(MainApplication.class, args);

        System.out.println("=================================================");
        System.out.println("Computer Shop Application Started Successfully.");
        System.out.println("=================================================");
        System.out.println("Access the application at http://localhost:2345");
        System.out.println("Default admin credentials - Username: admin | Password: 123456");
        System.out.println("=================================================");
    }

}
