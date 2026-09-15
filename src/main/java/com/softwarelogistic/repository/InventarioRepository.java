package com.softwarelogistic.repository;

import com.softwarelogistic.modelo.Inventario;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * ====================================================================
 * REPOSITORIO JPA: INVENTARIO FÍSICO
 * ====================================================================
 */
@Repository
public interface InventarioRepository extends JpaRepository<Inventario, Integer> {

    /**
     * Busca las existencias actuales de un producto por su ID.
     */
    Optional<Inventario> findByIdProducto(Integer idProducto);

    /**
     * Suma matemática de todas las unidades físicas disponibles en bodega.
     */
    @Query("SELECT COALESCE(SUM(i.cantidad), 0) FROM Inventario i")
    Integer sumarTotalUnidadesInventario();
}