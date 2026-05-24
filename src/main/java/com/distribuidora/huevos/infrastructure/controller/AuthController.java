package com.distribuidora.huevos.infrastructure.controller;

import com.distribuidora.huevos.application.dto.command.LoginRequest;
import com.distribuidora.huevos.application.dto.response.AuthResponse;
import com.distribuidora.huevos.infrastructure.security.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtUtil jwtUtil;
    private final String appUsername;
    private final String appPassword;

    public AuthController(
            JwtUtil jwtUtil,
            @Value("${app.username}") String appUsername,
            @Value("${app.password}") String appPassword) {
        this.jwtUtil = jwtUtil;
        this.appUsername = appUsername;
        this.appPassword = appPassword;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        if (!appUsername.equals(request.getUsername()) ||
            !appPassword.equals(request.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorMsg("Usuario o contraseña incorrectos"));
        }
        String token = jwtUtil.generarToken(request.getUsername());
        return ResponseEntity.ok(new AuthResponse(token, request.getUsername()));
    }

    record ErrorMsg(String mensaje) {}
}
