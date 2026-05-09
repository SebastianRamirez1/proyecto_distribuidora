package com.distribuidora.huevos.infrastructure.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Redirige todas las rutas del frontend (React Router) a index.html.
 * Sin este controller, al recargar /ventas o /clientes Spring devuelve 404.
 * Las rutas /api/** las maneja Spring normalmente (no llegan aquí).
 */
@Controller
public class SpaController {

    // Captura rutas sin extensión que NO empiezan con /api ni /h2-console
    @RequestMapping(value = {
            "/",
            "/dashboard",
            "/ventas",
            "/clientes",
            "/inventario",
            "/precios",
            "/reportes"
    })
    public String forwardToIndex() {
        return "forward:/index.html";
    }
}
