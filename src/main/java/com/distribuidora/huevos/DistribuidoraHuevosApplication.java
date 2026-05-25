package com.distribuidora.huevos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

// Excluimos UserDetailsServiceAutoConfiguration porque usamos JWT propio
// (JwtAuthFilter + JwtUtil) sin necesidad de UserDetailsService de Spring Security.
@SpringBootApplication(exclude = {UserDetailsServiceAutoConfiguration.class})
public class DistribuidoraHuevosApplication {

    public static void main(String[] args) {
        SpringApplication.run(DistribuidoraHuevosApplication.class, args);
    }
}
