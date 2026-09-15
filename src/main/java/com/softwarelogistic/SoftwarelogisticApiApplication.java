package com.softwarelogistic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ====================================================================
 * CLASE PRINCIPAL DE ENTRADA AL SISTEMA
 * ====================================================================
 * @SpringBootApplication activa:
 * 1. @Configuration: Permite registrar componentes adicionales.
 * 2. @EnableAutoConfiguration: Configura Tomcat, JPA y DataSource automáticamente.
 * 3. @ComponentScan: Escanea todos los controladores, servicios y repositorios
 *    dentro del paquete com.softwarelogistic.
 */
@SpringBootApplication
public class SoftwarelogisticApiApplication {

    public static void main(String[] args) {
        // Inicia el contenedor de Spring Boot y levanta el servidor web en el puerto 8080
        SpringApplication.run(SoftwarelogisticApiApplication.class, args);
    }
}