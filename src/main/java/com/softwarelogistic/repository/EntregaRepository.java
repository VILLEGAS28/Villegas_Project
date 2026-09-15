package com.softwarelogistic.repository;

import com.softwarelogistic.modelo.Entrega;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * ====================================================================
 * REPOSITORIO JPA: ENTREGAS Y DESPACHOS
 * ====================================================================
 */
@Repository
public interface EntregaRepository extends JpaRepository<Entrega, Integer> {

    /**
     * Busca la entrega asignada a un pedido específico.
     */
    Optional<Entrega> findByIdPedido(Integer idPedido);

    /**
     * Cuenta cuántas entregas se programaron o realizaron en un rango de fechas.
     */
    long countByFechaEntregaBetween(LocalDateTime inicio, LocalDateTime fin);
}