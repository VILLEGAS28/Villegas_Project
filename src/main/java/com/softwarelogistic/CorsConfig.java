package com.softwarelogistic;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * ====================================================================
 * CONFIGURACIÓN DE CORS (Cross-Origin Resource Sharing)
 * ====================================================================
 * Esta clase le dice al servidor Spring Boot qué aplicaciones externas,
 * páginas web y puertos tienen permiso para conectarse a nuestras APIs.
 * Sin esto, el navegador bloquea las llamadas por seguridad.
 */
@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                // Aplica esta regla a todas las rutas de la aplicación ("/**")
                registry.addMapping("/**")
                        
                        // Orígenes permitidos:
                        // 1. localhost:8080 (servidor embebido de Spring Boot)
                        // 2. localhost:5500 y 127.0.0.1:5500 (Live Server de VS Code)
                        // 3. localhost:3000 (por si usas React/Vue u otro cliente)
                        .allowedOriginPatterns(
                                "http://localhost:[*]",
                                "http://127.0.0.1:[*]"
                        )

                        // Métodos HTTP que permitimos que el Frontend ejecute:
                        // GET = Consultar datos
                        // POST = Crear registros
                        // PUT = Actualizar registros
                        // DELETE = Eliminar registros
                        // OPTIONS = Verificación previa del navegador
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")

                        // Permite enviar cualquier encabezado (Headers como Content-Type, Authorization)
                        .allowedHeaders("*")

                        // Permite enviar cookies, sesiones y credenciales de autenticación
                        .allowCredentials(true)
                        
                        // Tiempo que el navegador guarda en caché esta configuración (1 hora)
                        .maxAge(3600);
            }
        };
    }
}