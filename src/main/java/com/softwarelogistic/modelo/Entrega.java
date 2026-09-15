package com.softwarelogistic.modelo;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * ====================================================================
 * ENTIDAD JPA: ENTREGAS (TABLA 'entregas')
 * ====================================================================
 * Controla el despacho y recepción de los pedidos.
 */
@Entity
@Table(name = "entregas")
public class Entrega {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_entrega")
    private Integer idEntrega;

    @Column(name = "id_pedido", nullable = false)
    private Integer idPedido;

    @Column(name = "fecha_entrega")
    private LocalDateTime fechaEntrega;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoEntrega estado;

    @Column(name = "observacion", length = 255)
    private String observacion;

    public Entrega() {
    }

    @PrePersist
    protected void antesDeGuardar() {
        if (estado == null) {
            estado = EstadoEntrega.PENDIENTE;
        }
    }

    // Getters y Setters
    public Integer getIdEntrega() {
        return idEntrega;
    }

    public void setIdEntrega(Integer idEntrega) {
        this.idEntrega = idEntrega;
    }

    public Integer getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(Integer idPedido) {
        this.idPedido = idPedido;
    }

    public LocalDateTime getFechaEntrega() {
        return fechaEntrega;
    }

    public void setFechaEntrega(LocalDateTime fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }

    public EstadoEntrega getEstado() {
        return estado;
    }

    public void setEstado(EstadoEntrega estado) {
        this.estado = estado;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }
}