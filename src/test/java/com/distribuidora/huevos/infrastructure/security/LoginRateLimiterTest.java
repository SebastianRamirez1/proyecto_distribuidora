package com.distribuidora.huevos.infrastructure.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LoginRateLimiterTest {

    private LoginRateLimiter rateLimiter;

    @BeforeEach
    void setUp() {
        rateLimiter = new LoginRateLimiter();
    }

    @Test
    void permiteIntentosHastaElLimite() {
        for (int i = 0; i < LoginRateLimiter.MAX_INTENTOS; i++) {
            assertThat(rateLimiter.permitir("1.2.3.4"))
                    .as("Intento %d debe estar permitido", i + 1)
                    .isTrue();
        }
    }

    @Test
    void bloqueaAlSuperarElLimite() {
        for (int i = 0; i < LoginRateLimiter.MAX_INTENTOS; i++) {
            rateLimiter.permitir("1.2.3.4");
        }
        assertThat(rateLimiter.permitir("1.2.3.4"))
                .as("El intento %d debe estar bloqueado", LoginRateLimiter.MAX_INTENTOS + 1)
                .isFalse();
    }

    @Test
    void resetearLiberaLaIpBloqueada() {
        for (int i = 0; i <= LoginRateLimiter.MAX_INTENTOS; i++) {
            rateLimiter.permitir("1.2.3.4");
        }
        rateLimiter.resetear("1.2.3.4");

        assertThat(rateLimiter.permitir("1.2.3.4"))
                .as("Tras resetear, la IP debe poder intentar de nuevo")
                .isTrue();
    }

    @Test
    void diferentesIpsSonIndependientes() {
        // Bloquear la IP A
        for (int i = 0; i <= LoginRateLimiter.MAX_INTENTOS; i++) {
            rateLimiter.permitir("10.0.0.1");
        }
        // La IP B no debe verse afectada
        assertThat(rateLimiter.permitir("10.0.0.2"))
                .as("Una IP diferente no debe estar bloqueada")
                .isTrue();
    }
}
