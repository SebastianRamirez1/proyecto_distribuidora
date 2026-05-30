package com.distribuidora.huevos.infrastructure.controller;

import com.distribuidora.huevos.application.dto.command.LoginRequest;
import com.distribuidora.huevos.application.dto.response.AuthResponse;
import com.distribuidora.huevos.infrastructure.security.JwtUtil;
import com.distribuidora.huevos.infrastructure.security.LoginRateLimiter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final JwtUtil jwtUtil;
    private final LoginRateLimiter rateLimiter;
    private final String appUsername;
    private final String appPassword;

    public AuthController(
            JwtUtil jwtUtil,
            LoginRateLimiter rateLimiter,
            @Value("${app.username}") String appUsername,
            @Value("${app.password}") String appPassword) {
        this.jwtUtil      = jwtUtil;
        this.rateLimiter  = rateLimiter;
        this.appUsername  = appUsername;
        this.appPassword  = appPassword;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request,
                                   HttpServletRequest httpRequest) {
        String ip = resolverIp(httpRequest);

        // ── Rate limiting ─────────────────────────────────────────────────────
        if (!rateLimiter.permitir(ip)) {
            log.warn("[SECURITY] Login bloqueado por rate limit — IP: {}", ip);
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(new ErrorMsg("Demasiados intentos. Espera unos minutos e intenta de nuevo."));
        }

        // ── Validación de credenciales ────────────────────────────────────────
        if (!appUsername.equals(request.getUsername()) ||
            !appPassword.equals(request.getPassword())) {
            log.warn("[SECURITY] Login fallido — IP: {}, usuario: '{}'", ip, request.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorMsg("Usuario o contraseña incorrectos"));
        }

        // ── Login exitoso ─────────────────────────────────────────────────────
        rateLimiter.resetear(ip);
        log.info("[SECURITY] Login exitoso — IP: {}, usuario: '{}'", ip, request.getUsername());
        String token = jwtUtil.generarToken(request.getUsername());
        return ResponseEntity.ok(new AuthResponse(token, request.getUsername()));
    }

    /** Resuelve la IP real considerando proxies (Railway usa proxy). */
    private static String resolverIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    record ErrorMsg(String mensaje) {}
}
