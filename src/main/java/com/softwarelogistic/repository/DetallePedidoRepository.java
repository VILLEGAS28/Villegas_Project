package com.softwarelogistic.repository;

import com.softwarelogistic.modelo.DetallePedido;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio encargado de realizar las operaciones
 * de base de datos relacionadas con los detalles de pedido.
 */
@Repository
public interface DetallePedidoRepository
        extends JpaRepository<DetallePedido, Integer> {

    /**
     * Buscar todos los detalles pertenecientes a un pedido.
     */
    List<DetallePedido> findByIdPedido(Integer idPedido);

    /**
     * Buscar todos los detalles donde aparece un producto.
     */
    List<DetallePedido> findByIdProducto(Integer idProducto);
}