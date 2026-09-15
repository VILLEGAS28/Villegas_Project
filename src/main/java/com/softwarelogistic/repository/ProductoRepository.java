package com.softwarelogistic.repository;

import com.softwarelogistic.modelo.Producto;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * ====================================================================
 * REPOSITORIO JPA: PRODUCTOS
 * ====================================================================
 * Consultas sobre el catálogo maestro de mercancías.
 */
@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {

    /**
     * Busca un producto por su código de barras / referencia única.
     */
    Optional<Producto> findByCodigo(String codigo);

    /**
     * Verifica si ya existe un producto registrado con ese código.
     */
    boolean existsByCodigo(String codigo);

    /**
     * Busca productos cuyo nombre contenga el texto ignorando mayúsculas/minúsculas.
     */
    List<Producto> findByNombreContainingIgnoreCase(String nombre);

    /**
     * Obtiene únicamente los productos en estado activo.
     */
    List<Producto> findByEstadoTrue();
}