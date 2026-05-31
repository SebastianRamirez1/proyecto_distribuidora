package com.distribuidora.huevos.infrastructure.security;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Rate limiter de ventana deslizante para el endpoint de login.
 * Máximo {@link #MAX_INTENTOS} intentos fallidos por IP en {@link #VENTANA_MS} milisegundos.
 * Si se supera el límite, la IP queda bloqueada hasta que expire la ventana.
 *
 * <p>Sin dependencias externas (sin Redis, sin Bucket4j).
 * Suficiente para una app de un solo nodo como esta.
 */
@Component
public class LoginRateLimiter {

    static final int  MAX_INTENTOS = 10;
    static final long VENTANA_MS   = 5 * 60 * 1000L; // 5 minutos

    private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();

    /** @return true si la IP puede intentar login; false si está bloqueada. */
    public boolean permitir(String ip) {
        Bucket bucket = buckets.compute(ip, (k, b) -> {
            if (b == null || b.expirado()) return new Bucket();
            return b;
        });
        return bucket.intentar();
    }

    /** Resetea los intentos de una IP tras un login exitoso. */
    public void resetear(String ip) {
        buckets.remove(ip);
    }

    // ── Estado por IP ──────────────────────────────────────────────────────────

    private static class Bucket {
        private final Instant inicio = Instant.now();
        private int intentos = 0;

        boolean expirado() {
            return Instant.now().toEpochMilli() - inicio.toEpochMilli() > VENTANA_MS;
        }

        boolean intentar() {
            intentos++;
            return intentos <= MAX_INTENTOS;
        }
    }
}
