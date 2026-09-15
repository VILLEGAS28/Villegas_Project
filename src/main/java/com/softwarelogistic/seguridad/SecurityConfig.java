package com.softwarelogistic.seguridad;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

/**
 * ====================================================================
 * CONFIGURACIÓN PRINCIPAL DE SEGURIDAD HTTP (SPRING SECURITY)
 * ====================================================================
 * Controla el cortafuegos del sistema: autorizaciones de rutas, políticas
 * de CORS, protección CSRF y filtros de acceso a los endpoints REST.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            // 1. Integración de la política CORS unificada
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            // 2. Desactivación de CSRF (Cross-Site Request Forgery)
            // Es la práctica recomendada para APIs REST sin estado (Stateless)
            .csrf(csrf -> csrf.disable())

            // 3. Reglas de autorización de solicitudes HTTP
            .authorizeHttpRequests(auth -> auth
                // Permitir libre acceso a recursos estáticos del Frontend (HTML, CSS, JS, Imágenes)
                .requestMatchers(
                    "/",
                    "/*.html",
                    "/css/**",
                    "/js/**",
                    "/IMG/**",
                    "/favicon.ico"
                ).permitAll()

                // Permitir acceso a los endpoints de la API pública y autenticación
                .requestMatchers("/api/**").permitAll()

                // Cualquier otra solicitud está permitida en este entorno
                .anyRequest().permitAll()
            );

        return http.build();
    }

    /**
     * Configuración CORS definitiva y segura para la API.
     * Permite la comunicación tanto con el servidor embebido (8080)
     * como con clientes de desarrollo (Live Server 5500, Vite/React 3000 o 5173).
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        // Patrones de origen permitidos en local
        configuration.setAllowedOriginPatterns(
            Arrays.asList(
                "http://localhost:*",
                "http://127.0.0.1:*"
            )
        );

        // Métodos HTTP autorizados
        configuration.setAllowedMethods(
            Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS")
        );

        // Permitir todos los encabezados HTTP estándar y personalizados
        configuration.setAllowedHeaders(Collections.singletonList("*"));

        // Habilitar el intercambio seguro de credenciales (Cookies, tokens de sesión)
        configuration.setAllowCredentials(true);

        // Tiempo en caché de la comprobación previa OPTIONS del navegador (1 hora)
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}