package com.distribuidora.huevos.infrastructure.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Reenvía todas las rutas de client-side routing de la SPA a index.html.
 *
 * <p>Sin este controller, navegar directamente a /login, /ventas, /facturas, etc.
 * puede devolver 500 (Spring Security registra un handler en /login) o 404 para
 * rutas no listadas. Con este controller, React Router toma el control en el cliente.
 *
 * <p>Las rutas /api/** las maneja Spring normalmente y nunca llegan aquí.
 * El patrón /{path:[^\\.]*} excluye rutas con extensión (assets, .js, .css).
 */
@Controller
public class SpaController {

    /** Cualquier ruta de un nivel sin extensión → index.html */
    @GetMapping("/{path:[^\\.]*}")
    public String spa() {
        return "forward:/index.html";
    }
}
