package com.softwarelogistic.repository;

import com.softwarelogistic.modelo.EstadoPedido;
import com.softwarelogistic.modelo.Pedido;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio encargado de gestionar
 * los pedidos del sistema.
 */
public interface PedidoRepository
        extends JpaRepository<Pedido, Integer> {

    /**
     * Buscar pedidos de un cliente.
     */
    List<Pedido> findByIdCliente(Integer idCliente);

    /**
     * Buscar pedidos por estado.
     */
    List<Pedido> findByEstado(EstadoPedido estado);

    /**
     * Buscar pedidos de un cliente por estado.
     */
    List<Pedido> findByIdClienteAndEstado(
            Integer idCliente,
            EstadoPedido estado
    );

    /**
     * Contar pedidos según su estado.
     */
    long countByEstado(EstadoPedido estado);
}