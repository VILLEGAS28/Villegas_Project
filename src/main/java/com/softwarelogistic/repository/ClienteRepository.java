package com.softwarelogistic.repository;

import com.softwarelogistic.modelo.Cliente;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio encargado de realizar
 * las operaciones de base de datos
 * relacionadas con los clientes.
 */
@Repository
public interface ClienteRepository
        extends JpaRepository<Cliente, Integer> {

    // ---------------------------------------------------------
    // BUSCAR POR NOMBRE
    // ---------------------------------------------------------

    /**
     * Busca clientes cuyo nombre contenga
     * el texto indicado.
     */
    List<Cliente> findByNombreContainingIgnoreCase(
            String nombre
    );

    // ---------------------------------------------------------
    // BUSCAR POR CIUDAD
    // ---------------------------------------------------------

    /**
     * Busca clientes cuya ciudad contenga
     * el texto indicado.
     */
    List<Cliente> findByCiudadContainingIgnoreCase(
            String ciudad
    );

    // ---------------------------------------------------------
    // VALIDAR CORREO
    // ---------------------------------------------------------

    /**
     * Comprueba si ya existe un cliente
     * utilizando determinado correo.
     */
    boolean existsByCorreo(String correo);
}