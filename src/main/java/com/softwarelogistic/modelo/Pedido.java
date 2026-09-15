package com.softwarelogistic.modelo;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * ====================================================================
 * ENTIDAD JPA: PEDIDOS (TABLA 'pedidos')
 * ====================================================================
 * Representa la orden de compra maestro de un cliente.
 */
@Entity
@Table(name = "pedidos")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pedido")
    private Integer idPedido;

    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha;

    @Column(name = "id_cliente", nullable = false)
    private Integer idCliente;

    // Relación JPA para traer automáticamente los datos del cliente
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_cliente", insertable = false, updatable = false)
    private Cliente cliente;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoPedido estado;

    public Pedido() {
    }

    // Auditoría antes de insertar en base de datos
    @PrePersist
    protected void antesDeGuardar() {
        if (fecha == null) {
            fecha = LocalDateTime.now();
        }
        if (estado == null) {
            estado = EstadoPedido.PENDIENTE;
        }
    }

    // Getters y Setters
    public Integer getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(Integer idPedido) {
        this.idPedido = idPedido;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
        if (cliente != null) {
            this.idCliente = cliente.getIdCliente();
        }
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    public void setEstado(EstadoPedido estado) {
        this.estado = estado;
    }
}