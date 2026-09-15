package com.softwarelogistic.controlador;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ====================================================================
 * CONTROLADOR DE SALUD DE LA API (HEALTH CHECK)
 * ====================================================================
 * Sirve para probar rápidamente si el servidor Spring Boot está en línea
 * accediendo a http://localhost:8080/api desde cualquier navegador.
 */
@RestController
public class InicioController {

    @GetMapping("/api")
    public String inicio() {
        return "SoftwareLogistic API funcionando correctamente en Spring Boot";
    }
}