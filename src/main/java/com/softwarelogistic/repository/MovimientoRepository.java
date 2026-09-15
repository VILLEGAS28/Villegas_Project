package com.softwarelogistic.repository;

import com.softwarelogistic.modelo.Movimiento;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio encargado de gestionar
 * los movimientos del inventario.
 */
@Repository
public interface MovimientoRepository
        extends JpaRepository<Movimiento, Integer> {

    /**
     * Buscar movimientos de un producto.
     */
    List<Movimiento> findByIdProducto(Integer idProducto);

    /**
     * Buscar movimientos según el tipo.
     */
    List<Movimiento> findByTipo(
            com.softwarelogistic.modelo.TipoMovimiento tipo
    );

    /**
     * Buscar movimientos de un producto
     * filtrados por tipo.
     */
    List<Movimiento> findByIdProductoAndTipo(
            Integer idProducto,
            com.softwarelogistic.modelo.TipoMovimiento tipo
    );
}