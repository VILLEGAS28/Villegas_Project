package com.softwarelogistic.repository;

import com.softwarelogistic.modelo.Proveedor;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * ====================================================================
 * REPOSITORIO JPA: PROVEEDORES
 * ====================================================================
 * Consultas y validaciones para empresas proveedoras.
 */
@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor, Integer> {

    /**
     * Busca un proveedor por su número de identificación tributaria (NIT).
     */
    Optional<Proveedor> findByNit(String nit);

    /**
     * Busca un proveedor por su nombre o razón social.
     */
    Optional<Proveedor> findByNombre(String nombre);

    /**
     * Comprueba si un NIT ya está registrado.
     */
    boolean existsByNit(String nit);
}