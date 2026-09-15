package com.softwarelogistic.seguridad;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * ====================================================================
 * CONFIGURACIÓN DE ENCRIPTACIÓN DE CONTRASEÑAS
 * ====================================================================
 * Provee el bean PasswordEncoder utilizando el algoritmo criptográfico
 * unidireccional BCrypt con un factor de costo (salt) de seguridad estándar.
 */
@Configuration
public class PasswordConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Genera hashes seguros con salt aleatorio para evitar ataques de diccionario
        return new BCryptPasswordEncoder();
    }
}