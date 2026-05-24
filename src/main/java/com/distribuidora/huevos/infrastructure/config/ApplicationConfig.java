package com.distribuidora.huevos.infrastructure.config;

import org.springframework.context.annotation.Configuration;

// DECISIÓN: la configuración de JPA/DataSource se maneja 100% vía application.yml
// para mantener las propiedades visibles y sin código de infraestructura redundante.
// Spring Boot autoconfigura EntityManagerFactory, TransactionManager, etc.
@Configuration
public class ApplicationConfig {
    // Sin beans adicionales: Spring Boot Boot autoconfiguration lo cubre todo.
}
